package com.ly.infrastructure.persistent.repository;

import cn.xc.custom.db.router.strategy.IDBRouterStrategy;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ly.domain.activity.event.ActivitySkuStockZeroMessageEvent;
import com.ly.domain.activity.model.aggregate.CreateOrderAggregate;
import com.ly.domain.activity.model.aggregate.CreatePartakeOrderAggregate;
import com.ly.domain.activity.model.entity.*;
import com.ly.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.ly.domain.activity.model.vo.ActivityStateVO;
import com.ly.domain.activity.model.vo.UserRaffleOrderStateVO;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.infrastructure.event.EventPublisher;
import com.ly.infrastructure.persistent.dao.*;
import com.ly.infrastructure.persistent.po.*;
import com.ly.infrastructure.persistent.redis.IRedisService;
import com.ly.types.common.Constants;
import com.ly.types.enums.ResponseCode;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
@Slf4j
public class ActivityRepository implements IActivityRepository {

    @Resource
    private RaffleActivitySkuMapper raffleActivitySkuMapper;

    @Resource
    private IRedisService redisService;

    @Resource
    private RaffleActivityMapper raffleActivityMapper;

    @Resource
    private RaffleActivityCountMapper raffleActivityCountMapper;

    @Resource
    private RaffleActivityOrderMapper raffleActivityOrderMapper;

    @Resource
    private RaffleActivityAccountMapper raffleActivityAccountMapper;

    @Resource
    private RaffleActivityAccountMonthDao raffleActivityAccountMonthDao;

    @Resource
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Resource
    private UserRaffleOrderDao userRaffleOrderDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private IDBRouterStrategy routerStrategy;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private ActivitySkuStockZeroMessageEvent messageEvent;

    @Override
    public ActivitySkuEntity queryActivitySku(Long sku) {
        RaffleActivitySku raffleActivitySku = raffleActivitySkuMapper.selectOne(
                Wrappers.lambdaQuery(RaffleActivitySku.class)
                        .eq(RaffleActivitySku::getSku, sku));
        return ActivitySkuEntity.builder()
                .sku(raffleActivitySku.getSku())
                .activityId(raffleActivitySku.getActivityId())
                .activityCountId(raffleActivitySku.getActivityCountId())
                .stockCount(raffleActivitySku.getStockCount())
                .stockCountSurplus(raffleActivitySku.getStockCountSurplus())
                .build();
    }

    @Override
    public ActivityEntity queryRaffleActivityByActivityId(Long activityId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_KEY + activityId;
        ActivityEntity activityEntity = redisService.getValue(cacheKey);
        if (null != activityEntity) return activityEntity;
        // 从库中获取数据
        RaffleActivity raffleActivity = raffleActivityMapper.
                selectOne(Wrappers.lambdaQuery(RaffleActivity.class)
                        .eq(RaffleActivity::getActivityId, activityId));
        activityEntity = ActivityEntity.builder()
                .activityId(raffleActivity.getActivityId())
                .activityName(raffleActivity.getActivityName())
                .activityDesc(raffleActivity.getActivityDesc())
                .beginDateTime(raffleActivity.getBeginDateTime())
                .endDateTime(raffleActivity.getEndDateTime())
                .strategyId(raffleActivity.getStrategyId())
                .state(ActivityStateVO.valueOf(raffleActivity.getState()))
                .build();
        redisService.setValue(cacheKey, activityEntity);
        return activityEntity;
    }

    @Override
    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.ACTIVITY_COUNT_KEY + activityCountId;
        ActivityCountEntity activityCountEntity = redisService.getValue(cacheKey);
        if (null != activityCountEntity) return activityCountEntity;
        // 从库中获取数据
        RaffleActivityCount raffleActivityCount = raffleActivityCountMapper.
                selectOne(Wrappers.lambdaQuery(RaffleActivityCount.class)
                        .eq(RaffleActivityCount::getActivityCountId, activityCountId));
        activityCountEntity = ActivityCountEntity.builder()
                .activityCountId(raffleActivityCount.getActivityCountId())
                .totalCount(raffleActivityCount.getTotalCount())
                .dayCount(raffleActivityCount.getDayCount())
                .monthCount(raffleActivityCount.getMonthCount())
                .build();
        redisService.setValue(cacheKey, activityCountEntity);
        return activityCountEntity;
    }

    @Override
    public boolean doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        // 订单对象
        ActivityOrderEntity activityOrderEntity = createOrderAggregate.getActivityOrderEntity();
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId(activityOrderEntity.getUserId());
        raffleActivityOrder.setSku(activityOrderEntity.getSku());
        raffleActivityOrder.setActivityId(activityOrderEntity.getActivityId());
        raffleActivityOrder.setActivityName(activityOrderEntity.getActivityName());
        raffleActivityOrder.setStrategyId(activityOrderEntity.getStrategyId());
        raffleActivityOrder.setOrderId(activityOrderEntity.getOrderId());
        raffleActivityOrder.setOrderTime(activityOrderEntity.getOrderTime());
        raffleActivityOrder.setTotalCount(activityOrderEntity.getTotalCount());
        raffleActivityOrder.setDayCount(activityOrderEntity.getDayCount());
        raffleActivityOrder.setMonthCount(activityOrderEntity.getMonthCount());
        raffleActivityOrder.setState(activityOrderEntity.getState().getCode());
        raffleActivityOrder.setOutBusinessNo(activityOrderEntity.getOutBusinessNo());

        // 账户对象
        RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
        raffleActivityAccount.setUserId(createOrderAggregate.getUserId());
        raffleActivityAccount.setActivityId(createOrderAggregate.getActivityId());
        raffleActivityAccount.setTotalCount(createOrderAggregate.getTotalCount());
        raffleActivityAccount.setTotalCountSurplus(createOrderAggregate.getTotalCount());
        raffleActivityAccount.setDayCount(createOrderAggregate.getDayCount());
        raffleActivityAccount.setDayCountSurplus(createOrderAggregate.getDayCount());
        raffleActivityAccount.setMonthCount(createOrderAggregate.getMonthCount());
        raffleActivityAccount.setMonthCountSurplus(createOrderAggregate.getMonthCount());

        // 进行路由 - 使得路由组件里的 ThreadLocal 里是有分库信息的
        routerStrategy.doRouter(createOrderAggregate.getUserId());

        // 手动事务开启
        return transactionTemplate.execute(status -> {
            try {
                // 写入订单
                raffleActivityOrderMapper.insert(raffleActivityOrder);
                // 更新账户
                int cnt = raffleActivityAccountMapper.updateAccountQuota(raffleActivityAccount);
                if (cnt == 0) raffleActivityAccountMapper.insert(raffleActivityAccount);

                return true;
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                log.error("写入订单记录，唯一索引冲突 userId: {} activityId: {} sku: {}", activityOrderEntity.getUserId(), activityOrderEntity.getActivityId(), activityOrderEntity.getSku(), e);
                throw new AppException(ResponseCode.INDEX_DUP.getCode());
            } finally {
                routerStrategy.clear();
            }
        });
    }

    @Override
    public void cacheActivitySkuStockCount(String cacheKey, Integer stockCount) {
        if (redisService.isExists(cacheKey)) return;
        redisService.setAtomicLong(cacheKey, stockCount);
    }

    @Override
    public boolean subtractionActivitySkuStock(Long sku, String cacheKey, Date endDateTime) {
        long surplus = redisService.decr(cacheKey);
        if (surplus == 0) {
            eventPublisher.publish(messageEvent.topic(),
                    JSON.toJSONString(messageEvent.buildEventMessage(sku)));
            return false;
        } else if (surplus < 0) {
            // 库存小于0，恢复为0
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }

        // 1. 按照cacheKey decr 后的值，如 99、98、97 和 key 组成为库存锁的key进行使用。
        // 2. 加锁为了兜底，如果后续有恢复库存，手动处理等【运营是人来操作，会有这种情况发放，系统要做防护】，也不会超卖。因为所有的可用库存key，都被加锁了。
        // 3. 设置加锁时间为活动到期 + 延迟1天
        String lockKey = cacheKey + Constants.UNDERLINE + surplus;
        long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
        Boolean lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        if (!lock) {
            log.error("活动sku库存加锁失败 {}", lockKey);
        }
        return lock;
    }

    @Override
    public void activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO activitySkuStockKeyVO) {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> blockingQueue = redisService.getBlockingQueue(cacheKey);
        RDelayedQueue<ActivitySkuStockKeyVO> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(activitySkuStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public ActivitySkuStockKeyVO takeQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        return destinationQueue.poll();
    }

    @Override
    public void clearQueueValue() {
        String cacheKey = Constants.RedisKey.ACTIVITY_SKU_COUNT_QUERY_KEY;
        RBlockingQueue<ActivitySkuStockKeyVO> destinationQueue = redisService.getBlockingQueue(cacheKey);
        destinationQueue.clear();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        raffleActivitySkuMapper.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        raffleActivitySkuMapper.clearActivitySkuStock(sku);
    }

    @Override
    public UserRaffleOrderEntity queryNoUsedRaffleOrder(PartakeRaffleActivityEntity partakeRaffleActivityEntity) {
        // 查询数据
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(partakeRaffleActivityEntity.getUserId());
        userRaffleOrderReq.setActivityId(partakeRaffleActivityEntity.getActivityId());
        UserRaffleOrder userRaffleOrderRes = userRaffleOrderDao.queryNoUsedRaffleOrder(userRaffleOrderReq);
        if (null == userRaffleOrderRes) return null;
        // 封装结果
        UserRaffleOrderEntity userRaffleOrderEntity = new UserRaffleOrderEntity();
        userRaffleOrderEntity.setUserId(userRaffleOrderRes.getUserId());
        userRaffleOrderEntity.setActivityId(userRaffleOrderRes.getActivityId());
        userRaffleOrderEntity.setActivityName(userRaffleOrderRes.getActivityName());
        userRaffleOrderEntity.setStrategyId(userRaffleOrderRes.getStrategyId());
        userRaffleOrderEntity.setOrderId(userRaffleOrderRes.getOrderId());
        userRaffleOrderEntity.setOrderTime(userRaffleOrderRes.getOrderTime());
        userRaffleOrderEntity.setOrderState(UserRaffleOrderStateVO.valueOf(userRaffleOrderRes.getOrderState()));
        return userRaffleOrderEntity;
    }

    @Override
    public ActivityAccountEntity queryActivityAccountByUserId(String userId, Long activityId) {
        // 1. 查询账户
        RaffleActivityAccount raffleActivityAccountReq = new RaffleActivityAccount();
        raffleActivityAccountReq.setUserId(userId);
        raffleActivityAccountReq.setActivityId(activityId);
        RaffleActivityAccount raffleActivityAccountRes = raffleActivityAccountMapper.queryActivityAccountByUserId(raffleActivityAccountReq);
        if (null == raffleActivityAccountRes) return null;
        // 2. 转换对象
        return ActivityAccountEntity.builder()
                .userId(raffleActivityAccountRes.getUserId())
                .activityId(raffleActivityAccountRes.getActivityId())
                .totalCount(raffleActivityAccountRes.getTotalCount())
                .totalCountSurplus(raffleActivityAccountRes.getTotalCountSurplus())
                .dayCount(raffleActivityAccountRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountRes.getDayCountSurplus())
                .monthCount(raffleActivityAccountRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountRes.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountMonthEntity queryActivityAccountMonthByUserId(String userId, Long activityId, String month) {
        // 1. 查询账户
        RaffleActivityAccountMonth raffleActivityAccountMonthReq = new RaffleActivityAccountMonth();
        raffleActivityAccountMonthReq.setUserId(userId);
        raffleActivityAccountMonthReq.setActivityId(activityId);
        raffleActivityAccountMonthReq.setMonth(month);
        RaffleActivityAccountMonth raffleActivityAccountMonthRes = raffleActivityAccountMonthDao.queryActivityAccountMonthByUserId(raffleActivityAccountMonthReq);
        if (null == raffleActivityAccountMonthRes) return null;
        // 2. 转换对象
        return ActivityAccountMonthEntity.builder()
                .userId(raffleActivityAccountMonthRes.getUserId())
                .activityId(raffleActivityAccountMonthRes.getActivityId())
                .month(raffleActivityAccountMonthRes.getMonth())
                .monthCount(raffleActivityAccountMonthRes.getMonthCount())
                .monthCountSurplus(raffleActivityAccountMonthRes.getMonthCountSurplus())
                .build();
    }

    @Override
    public ActivityAccountDayEntity queryActivityAccountDayByUserId(String userId, Long activityId, String day) {
        // 1. 查询账户
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(day);
        RaffleActivityAccountDay raffleActivityAccountDayRes = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (null == raffleActivityAccountDayRes) return null;
        // 2. 转换对象
        return ActivityAccountDayEntity.builder()
                .userId(raffleActivityAccountDayRes.getUserId())
                .activityId(raffleActivityAccountDayRes.getActivityId())
                .day(raffleActivityAccountDayRes.getDay())
                .dayCount(raffleActivityAccountDayRes.getDayCount())
                .dayCountSurplus(raffleActivityAccountDayRes.getDayCountSurplus())
                .build();
    }

    @Override
    public void saveCreatePartakeOrderAggregate(CreatePartakeOrderAggregate createPartakeOrderAggregate) {
        String userId = createPartakeOrderAggregate.getUserId();
        Long activityId = createPartakeOrderAggregate.getActivityId();
        ActivityAccountEntity activityAccountEntity = createPartakeOrderAggregate.getActivityAccountEntity();
        ActivityAccountMonthEntity activityAccountMonthEntity = createPartakeOrderAggregate.getActivityAccountMonthEntity();
        ActivityAccountDayEntity activityAccountDayEntity = createPartakeOrderAggregate.getActivityAccountDayEntity();
        UserRaffleOrderEntity userRaffleOrderEntity = createPartakeOrderAggregate.getUserRaffleOrderEntity();

        // 统一切换路由，以下事务内的所有操作，都走一个路由
        routerStrategy.doRouter(userId);
        transactionTemplate.execute(status -> {
            try {
                // 1. 更新总账户
                int totalCount = raffleActivityAccountMapper.updateActivityAccountSubtractionQuota(
                        RaffleActivityAccount.builder()
                                .userId(userId)
                                .activityId(activityId)
                                .build());
                if (1 != totalCount) {
                    status.setRollbackOnly();
                    log.warn("写入创建参与活动记录，更新总账户额度不足，异常 userId: {} activityId: {}", userId, activityId);
                    throw new AppException(ResponseCode.ACCOUNT_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_QUOTA_ERROR.getInfo());
                }

                // 2. 创建或更新月账户，true - 存在则更新 false - 不存在则插入
                if (createPartakeOrderAggregate.isExistAccountMonth()) {
                    int updateMonthCount = raffleActivityAccountMonthDao.updateActivityAccountMonthSubtractionQuota(
                            RaffleActivityAccountMonth.builder()
                                    .userId(userId)
                                    .activityId(activityId)
                                    .month(activityAccountMonthEntity.getMonth())
                                    .build());
                    if (1 != updateMonthCount) {
                        // 未更新成功则回滚
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新月账户额度不足，异常 userId: {} activityId: {} month: {}", userId, activityId, activityAccountMonthEntity.getMonth());
                        throw new AppException(ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_MONTH_QUOTA_ERROR.getInfo());
                    }
                } else {
                    raffleActivityAccountMonthDao.insertActivityAccountMonth(RaffleActivityAccountMonth.builder()
                            .userId(activityAccountMonthEntity.getUserId())
                            .activityId(activityAccountMonthEntity.getActivityId())
                            .month(activityAccountMonthEntity.getMonth())
                            .monthCount(activityAccountMonthEntity.getMonthCount())
                            .monthCountSurplus(activityAccountMonthEntity.getMonthCountSurplus() - 1)
                            .build());
                    // 新创建月账户，则更新总账表中月镜像额度
                    raffleActivityAccountMapper.updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .monthCountSurplus(activityAccountEntity.getMonthCountSurplus())
                            .build());
                }

                // 3. 创建或更新日账户，true - 存在则更新，false - 不存在则插入
                if (createPartakeOrderAggregate.isExistAccountDay()) {
                    int updateDayCount = raffleActivityAccountDayDao.updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .day(activityAccountDayEntity.getDay())
                            .build());
                    if (1 != updateDayCount) {
                        // 未更新成功则回滚
                        status.setRollbackOnly();
                        log.warn("写入创建参与活动记录，更新日账户额度不足，异常 userId: {} activityId: {} day: {}", userId, activityId, activityAccountDayEntity.getDay());
                        throw new AppException(ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getCode(), ResponseCode.ACCOUNT_DAY_QUOTA_ERROR.getInfo());
                    }
                } else {
                    raffleActivityAccountDayDao.insertActivityAccountDay(RaffleActivityAccountDay.builder()
                            .userId(activityAccountDayEntity.getUserId())
                            .activityId(activityAccountDayEntity.getActivityId())
                            .day(activityAccountDayEntity.getDay())
                            .dayCount(activityAccountDayEntity.getDayCount())
                            .dayCountSurplus(activityAccountDayEntity.getDayCountSurplus() - 1)
                            .build());
                    // 新创建日账户，则更新总账表中日镜像额度
                    raffleActivityAccountMapper.updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount.builder()
                            .userId(userId)
                            .activityId(activityId)
                            .dayCountSurplus(activityAccountEntity.getDayCountSurplus())
                            .build());
                }

                // 4. 写入参与活动订单
                userRaffleOrderDao.insert(UserRaffleOrder.builder()
                        .userId(userRaffleOrderEntity.getUserId())
                        .activityId(userRaffleOrderEntity.getActivityId())
                        .activityName(userRaffleOrderEntity.getActivityName())
                        .strategyId(userRaffleOrderEntity.getStrategyId())
                        .orderId(userRaffleOrderEntity.getOrderId())
                        .orderTime(userRaffleOrderEntity.getOrderTime())
                        .orderState(userRaffleOrderEntity.getOrderState().getCode())
                        .build());

                return 1;
            } catch (DuplicateKeyException e) {
                status.setRollbackOnly();
                log.error("写入创建参与活动记录，唯一索引冲突 userId: {} activityId: {}", userId, activityId, e);
                return 0;
            } finally {
                routerStrategy.clear();
            }
        });
    }

    @Override
    public List<ActivitySkuEntity> queryActivitySkuListByActivityId(Long activityId) {
        List<RaffleActivitySku> raffleActivitySkus = raffleActivitySkuMapper.queryActivitySkuListByActivityId(activityId);
        List<ActivitySkuEntity> activitySkuEntities = new ArrayList<>(raffleActivitySkus.size());
        for (RaffleActivitySku raffleActivitySku : raffleActivitySkus) {
            ActivitySkuEntity activitySkuEntity = new ActivitySkuEntity();
            activitySkuEntity.setSku(raffleActivitySku.getSku());
            activitySkuEntity.setActivityCountId(raffleActivitySku.getActivityCountId());
            activitySkuEntity.setStockCount(raffleActivitySku.getStockCount());
            activitySkuEntity.setStockCountSurplus(raffleActivitySku.getStockCountSurplus());
            activitySkuEntities.add(activitySkuEntity);
        }
        return activitySkuEntities;
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId) {
        RaffleActivityAccountDay raffleActivityAccountDay = new RaffleActivityAccountDay();
        raffleActivityAccountDay.setActivityId(activityId);
        raffleActivityAccountDay.setUserId(userId);
        raffleActivityAccountDay.setDay(raffleActivityAccountDay.currentDay());
        Integer dayPartakeCount = raffleActivityAccountDayDao.queryRaffleActivityAccountDayPartakeCount(raffleActivityAccountDay);
        // 当日未参与抽奖则为0次
        return null == dayPartakeCount ? 0 : dayPartakeCount;
    }
}
