package com.ly.infrastructure.persistent.repository;

import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson.JSON;
import com.ly.domain.award.model.aggregate.UserAwardRecordAggregate;
import com.ly.domain.award.model.entity.TaskEntity;
import com.ly.domain.award.model.entity.UserAwardRecordEntity;
import com.ly.domain.award.repository.IAwardRepository;
import com.ly.infrastructure.event.EventPublisher;
import com.ly.infrastructure.persistent.dao.TaskDao;
import com.ly.infrastructure.persistent.dao.UserAwardRecordDao;
import com.ly.infrastructure.persistent.dao.UserRaffleOrderDao;
import com.ly.infrastructure.persistent.po.Task;
import com.ly.infrastructure.persistent.po.UserAwardRecord;
import com.ly.infrastructure.persistent.po.UserRaffleOrder;
import com.ly.types.enums.ResponseCode;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;


@Repository
@Slf4j
public class AwardRepository implements IAwardRepository {

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
                // 更新数据库记录，task 任务表
                taskDao.updateTaskSendMessageSuccess(task);
            } catch (Exception e) {
                log.error("写入中奖记录，发送MQ消息失败 userId: {} topic: {}", userId, task.getTopic());
                taskDao.updateTaskSendMessageFail(task);
            }
        });

    }

}
