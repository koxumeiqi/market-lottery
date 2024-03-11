package com.ly.domain.strategy.service.rule.chain.impl;


import com.ly.domain.strategy.service.armory.IStrategyDispatch;
import com.ly.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(LogicModelConstants.LOGIC_DEFAULT)
@Slf4j
public class DefaultLogicChain extends AbstractLogicChain {

    @Autowired
    private IStrategyDispatch strategyDispatch;

    @Override
    public Integer logic(String userId, Long strategyId) {
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);
        log.info("抽奖责任链-默认处理 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
        return awardId;
    }


    @Override
    protected String ruleModel() {
        return LogicModelConstants.LOGIC_DEFAULT;
    }
}
