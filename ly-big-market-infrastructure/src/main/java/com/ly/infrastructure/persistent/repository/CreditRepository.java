package com.ly.infrastructure.persistent.repository;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.ly.domain.award.model.vo.AccountStatusVO;
import com.ly.domain.credit.model.aggregate.TradeAggregate;
import com.ly.domain.credit.model.entity.CreditAccountEntity;
import com.ly.domain.credit.model.entity.CreditOrderEntity;
import com.ly.domain.credit.model.entity.TaskEntity;
import com.ly.domain.credit.repository.ICreditRepository;
import com.ly.infrastructure.event.EventPublisher;
import com.ly.infrastructure.persistent.dao.IUserCreditAccountDao;
import com.ly.infrastructure.persistent.dao.IUserCreditOrderDao;
import com.ly.infrastructure.persistent.dao.TaskDao;
import com.ly.infrastructure.persistent.po.Task;
import com.ly.infrastructure.persistent.po.UserCreditAccount;
import com.ly.infrastructure.persistent.po.UserCreditOrder;
import com.ly.infrastructure.persistent.redis.IRedisService;
import com.ly.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Repository
@Slf4j
public class CreditRepository implements ICreditRepository {

    @Resource
    private IDBRouterStrategy routerStrategy;

    @Resource
    private IUserCreditOrderDao userCreditOrderDao;

    @Resource
    private IUserCreditAccountDao userCreditAccountDao;

    @Resource
    private IRedisService redisService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private TaskDao taskDao;

    @Resource
    private EventPublisher eventPublisher;

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();
        TaskEntity taskEntity = tradeAggregate.getTaskEntity();

        // 积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());

        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        int flag = 1;
        try {
            lock.lock(3, TimeUnit.SECONDS);
            routerStrategy.doRouter(userId);
            // 编程式事务
            flag = transactionTemplate.execute(status -> {
                try {
                    // 1. 保存账户积分
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccount) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    }
                    // 2. 保存账户订单
                    userCreditOrderDao.insert(userCreditOrderReq);
                    // 3. 写入任务
                    taskDao.insert(task);
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                    return 0;
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                    return 0;
                }
            });
        } finally {
            routerStrategy.clear();
            lock.unlock();
        }

        if (flag == 1) {
            try {
                // 发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(task.getTopic(), task.getMessage());
                // 更新数据库记录，task 任务表
                taskDao.updateTaskSendMessageSuccess(task);
                log.info("调整账户积分记录，发送MQ消息完成 userId: {} orderId:{} topic: {}", userId, creditOrderEntity.getOrderId(), task.getTopic());
            } catch (Exception e) {
                log.error("调整账户积分记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        }

    }

    @Override
    public CreditAccountEntity queryUserCreditAccount(String userId) {
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        try {
            routerStrategy.doRouter(userId);
            UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
            return CreditAccountEntity
                    .builder()
                    .userId(userId)
                    .adjustAmount(Objects.isNull(userCreditAccount) ? BigDecimal.ZERO : userCreditAccount.getAvailableAmount())
                    .build();
        } finally {
            routerStrategy.clear();
        }
    }


}
