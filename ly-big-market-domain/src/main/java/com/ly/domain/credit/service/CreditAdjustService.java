package com.ly.domain.credit.service;

import com.ly.domain.credit.model.aggregate.TradeAggregate;
import com.ly.domain.credit.model.entity.CreditAccountEntity;
import com.ly.domain.credit.model.entity.CreditOrderEntity;
import com.ly.domain.credit.model.entity.TradeEntity;
import com.ly.domain.credit.repository.ICreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class CreditAdjustService implements ICreditAdjustService {

    @Resource
    private ICreditRepository repository;

    @Override
    public String createOrder(TradeEntity tradeEntity) {
        String userId = tradeEntity.getUserId();
        // 1. 创建账户对象
        CreditAccountEntity creditAccountEntity = TradeAggregate.createCreditAccountEntity(
                tradeEntity.getUserId(),
                tradeEntity.getAmount());
        // 2. 构建订单实体
        CreditOrderEntity creditOrderEntity = TradeAggregate.createCreditOrderEntity(
                tradeEntity.getUserId(),
                tradeEntity.getTradeName(),
                tradeEntity.getTradeType(),
                tradeEntity.getAmount(),
                tradeEntity.getOutBusinessNo());
        // 3. 构建聚合对象
        TradeAggregate aggregate = new TradeAggregate();
        aggregate.setCreditOrderEntity(creditOrderEntity);
        aggregate.setUserId(userId);
        aggregate.setCreditAccountEntity(creditAccountEntity);
        // 4. 保存积分交易订单
        repository.saveUserCreditTradeOrder(aggregate);
        log.info("增加账户积分额度完成 userId:{} orderId:{}", tradeEntity.getUserId(), creditOrderEntity.getOrderId());

        return creditOrderEntity.getOrderId();
    }

}
