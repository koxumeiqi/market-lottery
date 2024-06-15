package com.ly.domain.strategy.service.rule.tree.factory.engine;

import com.ly.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import java.util.Date;

public interface IDecisionTreeEngine {

    DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId, Date endDateTime);

}
