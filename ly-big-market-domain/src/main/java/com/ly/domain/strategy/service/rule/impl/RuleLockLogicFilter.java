package com.ly.domain.strategy.service.rule.impl;

import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.model.entity.RuleMatterEntity;
import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.annotation.LogicStrategy;
import com.ly.domain.strategy.service.rule.ILogicFilter;
import com.ly.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_CENTER_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RuleActionEntity.CenterRaffleEntity> {

    @Autowired
    private IStrategyRepository repository;

    @Override
    public RuleActionEntity<RuleActionEntity.CenterRaffleEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        Integer awardId = ruleMatterEntity.getAwardId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        // 获取到这个奖品对应锁定的次数
        String ruleValue = repository.getLockCount(awardId, strategyId, ruleModel);
        Long lockCount = Long.valueOf(ruleValue);
        // 判断是否存在，和是否大于限定值，是的话放行
        // 配合Redis看还差这个奖品可以解锁
        Long userCount = repository.rQueryLockCount(userId, awardId, strategyId, lockCount);
        // 用户抽奖次数达标了
        if (userCount > lockCount) {
            return RuleActionEntity.<RuleActionEntity.CenterRaffleEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        // 用户抽奖次数没达标
        return RuleActionEntity.<RuleActionEntity.CenterRaffleEntity>builder()
                .data(
                        RuleActionEntity.CenterRaffleEntity.builder()
                                .raffleTimes(userCount)
                                .build()
                )
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .build();
    }
}
