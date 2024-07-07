package com.ly.domain.activity.service.quota.policy;

import com.ly.domain.activity.model.aggregate.CreateOrderAggregate;

/**
 * 交易策略
 */
public interface ITradePolicy {

    void trade(CreateOrderAggregate createQuotaOrderAggregate);

}
