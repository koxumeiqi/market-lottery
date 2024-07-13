package com.ly.domain.credit.service;

import com.ly.domain.credit.model.entity.CreditAccountEntity;
import com.ly.domain.credit.model.entity.TradeEntity;

/**
 * 积分分发服务
 */
public interface ICreditAdjustService {

    /**
     * 创建增加积分额度订单
     * @param tradeEntity 交易实体对象
     * @return 单号
     */
    String createOrder(TradeEntity tradeEntity);

    CreditAccountEntity queryUserCreditAccount(String userId);

}
