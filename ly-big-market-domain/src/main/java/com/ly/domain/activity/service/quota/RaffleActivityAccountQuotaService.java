package com.ly.domain.activity.service.quota;


import com.ly.domain.activity.model.aggregate.CreateOrderAggregate;
import com.ly.domain.activity.model.entity.*;
import com.ly.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.ly.domain.activity.model.vo.OrderStateVO;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.domain.activity.service.IRaffleActivitySkuStockService;
import com.ly.domain.activity.service.quota.rule.factory.ActionChainFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RaffleActivityAccountQuotaService extends AbstractRaffleActivityAccountQuota implements IRaffleActivitySkuStockService {

    public RaffleActivityAccountQuotaService(ActionChainFactory actionChainFactory, IActivityRepository repository) {
        super(actionChainFactory, repository);
    }

    @Override
    protected boolean doSaveOrder(CreateOrderAggregate createOrderAggregate) {
        return repository.doSaveOrder(createOrderAggregate);
    }

    @Override
    protected CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity skuEntity, ActivityEntity activityEntity, ActivityCountEntity countEntity) {

        CreateOrderAggregate createOrderAggr = CreateOrderAggregate.builder()
                .userId(skuRechargeEntity.getUserId())
                .activityId(activityEntity.getActivityId())
                .totalCount(countEntity.getTotalCount())
                .dayCount(countEntity.getDayCount())
                .monthCount(countEntity.getMonthCount())
                .build();
        // 构建订单对象
        ActivityOrderEntity orderEntity = new ActivityOrderEntity();
        orderEntity.setUserId(skuRechargeEntity.getUserId());
        orderEntity.setActivityId(activityEntity.getActivityId());
        orderEntity.setActivityName(activityEntity.getActivityName());
        orderEntity.setStrategyId(activityEntity.getStrategyId());
        orderEntity.setOutBusinessNo(skuRechargeEntity.getOutBusinessNo());
        orderEntity.setSku(skuEntity.getSku());
        orderEntity.setTotalCount(countEntity.getTotalCount());
        orderEntity.setDayCount(countEntity.getDayCount());
        orderEntity.setMonthCount(countEntity.getMonthCount());
        // 公司里一般会有专门的雪花算法UUID服务，我们这里直接生成个12位就可以了。
        orderEntity.setOrderId(RandomStringUtils.randomNumeric(12));
        orderEntity.setOrderTime(new Date());
        orderEntity.setState(OrderStateVO.completed);

        createOrderAggr.setActivityOrderEntity(orderEntity);
        return createOrderAggr;
    }


    @Override
    public ActivitySkuStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void clearQueueValue() {
        repository.clearQueueValue();
    }

    @Override
    public void updateActivitySkuStock(Long sku) {
        repository.updateActivitySkuStock(sku);
    }

    @Override
    public void clearActivitySkuStock(Long sku) {
        repository.clearActivitySkuStock(sku);
    }

    @Override
    public Integer queryRaffleActivityAccountDayPartakeCount(Long activityId, String userId) {
        return repository.queryRaffleActivityAccountDayPartakeCount(activityId, userId);
    }
}
