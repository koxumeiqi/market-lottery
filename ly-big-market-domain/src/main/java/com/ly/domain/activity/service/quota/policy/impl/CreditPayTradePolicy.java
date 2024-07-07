package com.ly.domain.activity.service.quota.policy.impl;

import com.ly.domain.activity.model.aggregate.CreateOrderAggregate;
import com.ly.domain.activity.model.vo.OrderStateVO;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.domain.activity.service.quota.policy.ITradePolicy;
import org.springframework.stereotype.Service;

/**
 * @description 积分兑换，支付类订单
 * @create 2024-06-08 18:12
 */
@Service("credit_pay_trade")
public class CreditPayTradePolicy implements ITradePolicy {

    private final IActivityRepository activityRepository;

    public CreditPayTradePolicy(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Override
    public void trade(CreateOrderAggregate createQuotaOrderAggregate) {
        createQuotaOrderAggregate.setOrderState(OrderStateVO.wait_pay);
        activityRepository.doSaveCreditPayOrder(createQuotaOrderAggregate);
    }

}
