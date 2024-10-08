package com.ly.infrastructure.persistent.repository;

import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.ly.domain.award.model.aggregate.GiveOutPrizesAggregate;
import com.ly.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.ly.domain.award.model.entity.AwardShowEntity;
import com.ly.domain.award.model.entity.TaskEntity;
import com.ly.domain.award.model.entity.UserAwardRecordEntity;
import com.ly.domain.award.model.entity.UserCreditAwardEntity;
import com.ly.domain.award.model.vo.AccountStatusVO;
import com.ly.domain.award.model.vo.AwardStateVO;
import com.ly.domain.award.repository.IAwardRepository;
import com.ly.infrastructure.event.EventPublisher;
import com.ly.infrastructure.persistent.dao.*;
import com.ly.infrastructure.persistent.po.Task;
import com.ly.infrastructure.persistent.po.UserAwardRecord;
import com.ly.infrastructure.persistent.po.UserCreditAccount;
import com.ly.infrastructure.persistent.po.UserRaffleOrder;
import com.ly.infrastructure.persistent.redis.IRedisService;
import com.ly.types.common.Constants;
import com.ly.types.common.DateUtil;
import com.ly.types.enums.ResponseCode;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.core.env.Environment;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Repository
@Slf4j
public class AwardRepository implements IAwardRepository {

    @Resource
    private Environment environment;

    @Resource
    private IUserCreditAccountDao userCreditAccountDao;

    @Resource
    private IAwardDao awardDao;

    @Resource
    private IDBRouterStrategy routerStrategy;

    @Resource
    private EventPublisher eventPublisher;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Resource
    private UserAwardRecordDao userAwardRecordDao;

    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;

    @Resource
    private TaskDao taskDao;

    @Resource
    private IRedisService redisService;


    @Override
    public void saveUserAwardRecord(UserAwardRecordAggregate userAwardRecordAggregate) {
        UserAwardRecordEntity userAwardRecordEntity = userAwardRecordAggregate.getUserAwardRecordEntity();
        TaskEntity taskEntity = userAwardRecordAggregate.getTaskEntity();
        String userId = userAwardRecordEntity.getUserId();
        Long activityId = userAwardRecordEntity.getActivityId();
        Integer awardId = userAwardRecordEntity.getAwardId();

        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId(userAwardRecordEntity.getUserId());
        userAwardRecord.setActivityId(userAwardRecordEntity.getActivityId());
        userAwardRecord.setStrategyId(userAwardRecordEntity.getStrategyId());
        userAwardRecord.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecord.setAwardId(userAwardRecordEntity.getAwardId());
        userAwardRecord.setAwardTitle(userAwardRecordEntity.getAwardTitle());
        userAwardRecord.setAwardTime(userAwardRecordEntity.getAwardTime());
        userAwardRecord.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        Task task = new Task();
        task.setUserId(taskEntity.getUserId());
        task.setTopic(taskEntity.getTopic());
        task.setMessageId(taskEntity.getMessageId());
        task.setMessage(JSON.toJSONString(taskEntity.getMessage()));
        task.setState(taskEntity.getState().getCode());

        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(userAwardRecordEntity.getUserId());
        userRaffleOrderReq.setOrderId(userAwardRecordEntity.getOrderId());

        try {
            routerStrategy.doRouter(userId);
            transactionTemplate.execute(status -> {
                try {
                    // 写入中奖记录
                    userAwardRecordDao.insertUserAwardRecord(userAwardRecord);
                    // 写入任务
                    taskDao.insert(task);
                    // 更新抽奖单
                    int count = userRaffleOrderDao.updateUserRaffleOrderStateUsed(userRaffleOrderReq);
                    if (1 != count) {
                        status.setRollbackOnly();
                        log.error("写入中奖记录，用户抽奖单已使用过，不可重复抽奖 userId: {} activityId: {} awardId: {}", userId, activityId, awardId);
                        throw new AppException(ResponseCode.ACTIVITY_ORDER_ERROR.getCode(), ResponseCode.ACTIVITY_ORDER_ERROR.getInfo());
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("写入中奖记录，唯一索引冲突 userId: {} activityId: {} awardId: {}", userId, activityId, awardId, e);
                    return 0;
                }
            });
        } finally {
            routerStrategy.clear();
        }

        threadPoolExecutor.execute(() -> {
            try {
                // 发送消息【在事务外执行，如果失败还有任务补偿】
                eventPublisher.publish(task.getTopic(), task.getMessage());
                log.info("写入中奖记录，发送MQ消息完成 userId: {} orderId:{} topic: {}", userId, userAwardRecordEntity.getOrderId(), task.getTopic());
                // 更新数据库记录，task 任务表
                taskDao.updateTaskSendMessageSuccess(task);
            } catch (Exception e) {
                log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        });

    }

    @Override
    public String queryAwardConfig(Integer awardId) {
        return awardDao.queryAwardConfig(awardId);
    }

    @Override
    public void saveGiveOutPrizesAggregate(GiveOutPrizesAggregate giveOutPrizesAggregate) {
        String userId = giveOutPrizesAggregate.getUserId();
        UserCreditAwardEntity userCreditAwardEntity = giveOutPrizesAggregate.getUserCreditAwardEntity();
        UserAwardRecordEntity userAwardRecordEntity = giveOutPrizesAggregate.getUserAwardRecordEntity();

        // 更新发奖记录
        UserAwardRecord userAwardRecordReq = new UserAwardRecord();
        userAwardRecordReq.setUserId(userId);
        userAwardRecordReq.setOrderId(userAwardRecordEntity.getOrderId());
        userAwardRecordReq.setAwardState(userAwardRecordEntity.getAwardState().getCode());

        // 更新用户积分 「首次则插入数据」
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userCreditAwardEntity.getUserId());
        userCreditAccountReq.setTotalAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAvailableAmount(userCreditAwardEntity.getCreditAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());

        RLock lock = redisService.getLock(Constants.RedisKey.ACTIVITY_ACCOUNT_LOCK + userId);
        try {
            lock.lock(3, TimeUnit.SECONDS);
            routerStrategy.doRouter(giveOutPrizesAggregate.getUserId());
            transactionTemplate.execute(status -> {
                try {
                    // 更新积分 || 创建积分账户
                    UserCreditAccount userCreditAccountRes = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (null == userCreditAccountRes) {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    }

                    // 更新奖品记录
                    int updateAwardCount = userAwardRecordDao.updateAwardRecordCompletedState(userAwardRecordReq);
                    if (0 == updateAwardCount) {
                        log.warn("更新中奖记录，重复更新拦截 userId:{} giveOutPrizesAggregate:{}", userId, JSON.toJSONString(giveOutPrizesAggregate));
                        status.setRollbackOnly();
                    }
                    return 1;
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("更新中奖记录，唯一索引冲突 userId: {} ", userId, e);
                    throw new AppException(ResponseCode.INDEX_DUP.getCode(), e);
                }
            });
        } finally {
            routerStrategy.clear();
            lock.unlock();
        }
    }

    @Override
    public String queryAwardKey(Integer awardId) {
        return awardDao.queryAwardKey(awardId);
    }

    @Override
    public List<AwardShowEntity> queryAwardListByActivityId(Long activityId) {
        String dbRouterKey = "custom-db-router.jdbc.datasource.list";
        String dbPropertyStr = environment.getProperty(dbRouterKey);
        int dbLen = dbPropertyStr.split(",").length;
        List<UserAwardRecord> userAwardRecords = new ArrayList<>();
        for (int index = 1; index <= dbLen; ++index) {
            routerStrategy.setDBKey(index);
            try {
                List<UserAwardRecord> userAwardRecordList = userAwardRecordDao.queryAwardListWithActivityId(activityId);
                userAwardRecords.addAll(userAwardRecordList);
            } finally {
                routerStrategy.clear();
            }
        }
        Collections.shuffle(userAwardRecords); // 展示乱序
        return userAwardRecords.stream().map(userAwardRecord ->
                AwardShowEntity.builder()
                        .awardId(userAwardRecord.getAwardId())
                        .awardStatus(AwardStateVO.getAwardStateVO(userAwardRecord.getAwardState()))
                        .awardTime(DateUtil.date2String(userAwardRecord.getAwardTime()))
                        .awardTitle(userAwardRecord.getAwardTitle())
                        .userId(userAwardRecord.getUserId())
                        .build()
        ).collect(Collectors.toList());
    }

}
