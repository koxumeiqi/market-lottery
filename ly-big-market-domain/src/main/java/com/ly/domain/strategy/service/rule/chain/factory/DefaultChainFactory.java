package com.ly.domain.strategy.service.rule.chain.factory;


import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.chain.ILogicChain;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Stack;

@Component
@Slf4j
public class DefaultChainFactory {

    private final Map<String, ILogicChain> logicChainGroup;

    protected final IStrategyRepository repository;

    public DefaultChainFactory(Map<String, ILogicChain> logicChainGroup, IStrategyRepository repository) {
        this.logicChainGroup = logicChainGroup;
        this.repository = repository;
    }

    public ILogicChain openLogicChain(Long strategyId) {
        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);
        String[] ruleModels = strategy.ruleModels();
        if (ruleModels == null || ruleModels.length == 0) return logicChainGroup.get(LogicModelConstants.LOGIC_DEFAULT);
        // 按照配置顺序装填用户配置的责任链；rule_blacklist、rule_weight 「注意此数据从Redis缓存中获取，如果更新库表，记得在测试阶段手动处理缓存」
        ILogicChain logicChain = logicChainGroup.get(ruleModels[0]);
        ILogicChain currentLogicChain = logicChain;
        for (int i = 1; i < ruleModels.length; i++) {
            ILogicChain nextChain = logicChainGroup.get(ruleModels[i]);
            currentLogicChain = currentLogicChain.appendNext(nextChain);
        }
        currentLogicChain.appendNext(logicChainGroup.get(LogicModelConstants.LOGIC_DEFAULT));
        return logicChain;
    }

}
