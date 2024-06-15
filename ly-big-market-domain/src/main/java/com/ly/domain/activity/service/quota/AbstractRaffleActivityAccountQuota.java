package com.ly.domain.activity.service.quota;

import com.ly.domain.activity.model.aggregate.CreateOrderAggregate;
import com.ly.domain.activity.model.entity.*;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.ly.domain.activity.service.quota.rule.factory.ActionChainFactory;
import com.ly.types.enums.ResponseCode;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public abstract class AbstractRaffleActivityAccountQuota extends RaffleActivityAccountQuotaSupport implements IRaffleActivityAccountQuotaService {


    public AbstractRaffleActivityAccountQuota(ActionChainFactory actionChainFactory, IActivityRepository repository) {
        super(actionChainFactory, repository);
    }

    @Override
    public String createOrder(SkuRechargeEntity skuRechargeEntity) {
        // 参数校验
        String userId = skuRechargeEntity.getUserId();
        Long sku = skuRechargeEntity.getSku();
        String outBusinessNo = skuRechargeEntity.getOutBusinessNo();
        if (null == sku || StringUtils.isBlank(userId) || StringUtils.isBlank(outBusinessNo)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 1. 查找活动相关信息：活动信息、活动次数信息
        ActivitySkuEntity skuEntity = this.queryActivitySku(sku);
        ActivityEntity activityEntity = this.queryRaffleActivityByActivityId(skuEntity.getActivityId());
        ActivityCountEntity countEntity = this.queryRaffleActivityCountByActivityCountId(skuEntity.getActivityCountId());
        // 2. 活动规则校验
        boolean success = this.checkRule(skuEntity, activityEntity, countEntity);
        // 3. 构建聚合对象 - 订单聚合对象
        CreateOrderAggregate createOrderAggregate = buildOrderAggregate(skuRechargeEntity, skuEntity, activityEntity, countEntity);
        // 4. 保存订单
        doSaveOrder(createOrderAggregate);
        return createOrderAggregate.getActivityOrderEntity().getOrderId();
    }

    protected abstract boolean doSaveOrder(CreateOrderAggregate createOrderAggregate);

    protected abstract CreateOrderAggregate buildOrderAggregate(SkuRechargeEntity skuRechargeEntity, ActivitySkuEntity skuEntity, ActivityEntity activityEntity, ActivityCountEntity countEntity);
}
