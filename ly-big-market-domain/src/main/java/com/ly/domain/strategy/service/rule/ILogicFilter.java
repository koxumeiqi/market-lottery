package com.ly.domain.strategy.service.rule;

import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.model.entity.RuleMatterEntity;

public interface ILogicFilter<T extends RuleActionEntity.RaffleEntity> {

    RuleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);

}