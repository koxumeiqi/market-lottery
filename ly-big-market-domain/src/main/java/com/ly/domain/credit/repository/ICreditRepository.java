package com.ly.domain.credit.repository;


import com.ly.domain.credit.model.aggregate.TradeAggregate;

/**
 * @description 用户积分仓储
 * @create 2024-06-01 09:11
 */
public interface ICreditRepository {

    void saveUserCreditTradeOrder(TradeAggregate tradeAggregate);

}
