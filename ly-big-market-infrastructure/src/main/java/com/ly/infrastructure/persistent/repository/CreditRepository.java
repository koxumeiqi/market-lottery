package com.ly.infrastructure.persistent.repository;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.ly.domain.award.model.vo.AccountStatusVO;
import com.ly.domain.credit.model.aggregate.TradeAggregate;
import com.ly.domain.credit.model.entity.CreditAccountEntity;
import com.ly.domain.credit.model.entity.CreditOrderEntity;
import com.ly.domain.credit.repository.ICreditRepository;
import com.ly.infrastructure.persistent.dao.IUserCreditAccountDao;
import com.ly.infrastructure.persistent.dao.IUserCreditOrderDao;
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

    @Override
    public void saveUserCreditTradeOrder(TradeAggregate tradeAggregate) {
        String userId = tradeAggregate.getUserId();
        CreditAccountEntity creditAccountEntity = tradeAggregate.getCreditAccountEntity();
        CreditOrderEntity creditOrderEntity = tradeAggregate.getCreditOrderEntity();

        // 积分账户
        UserCreditAccount userCreditAccountReq = new UserCreditAccount();
        userCreditAccountReq.setUserId(userId);
        userCreditAccountReq.setTotalAmount(creditAccountEntity.getAdjustAmount());
        // 知识；仓储往上有业务语义，仓储往下到 dao 操作是没有业务语义的。所以不用在乎这块使用的字段名称，直接用持久化对象即可。
        userCreditAccountReq.setAvailableAmount(creditAccountEntity.getAdjustAmount());
        userCreditAccountReq.setAccountStatus(AccountStatusVO.open.getCode());

        // 积分订单
        UserCreditOrder userCreditOrderReq = new UserCreditOrder();
        userCreditOrderReq.setUserId(creditOrderEntity.getUserId());
        userCreditOrderReq.setOrderId(creditOrderEntity.getOrderId());
        userCreditOrderReq.setTradeName(creditOrderEntity.getTradeName().getName());
        userCreditOrderReq.setTradeType(creditOrderEntity.getTradeType().getCode());
        userCreditOrderReq.setTradeAmount(creditOrderEntity.getTradeAmount());
        userCreditOrderReq.setOutBusinessNo(creditOrderEntity.getOutBusinessNo());

        RLock lock = redisService.getLock(Constants.RedisKey.USER_CREDIT_ACCOUNT_LOCK + userId + Constants.UNDERLINE + creditOrderEntity.getOutBusinessNo());
        try {
            lock.lock(3, TimeUnit.SECONDS);
            routerStrategy.doRouter(userId);
            // 编程式事务
            transactionTemplate.execute(status -> {
                try {
                    // 保存积分账户
                    UserCreditAccount userCreditAccount = userCreditAccountDao.queryUserCreditAccount(userCreditAccountReq);
                    if (userCreditAccount != null) {
                        userCreditAccountDao.updateAddAmount(userCreditAccountReq);
                    } else {
                        userCreditAccountDao.insert(userCreditAccountReq);
                    }
                    // 保存积分订单
                    userCreditOrderDao.insert(userCreditOrderReq);
                } catch (DuplicateKeyException e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度异常，唯一索引冲突 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                } catch (Exception e) {
                    status.setRollbackOnly();
                    log.error("调整账户积分额度失败 userId:{} orderId:{}", userId, creditOrderEntity.getOrderId(), e);
                }
                return 1;
            });
        } finally {
            routerStrategy.clear();
            lock.unlock();
        }

    }


}
