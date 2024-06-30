package com.ly.domain.strategy.service.rule.chain.factory;


import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.chain.ILogicChain;
import com.ly.types.common.LogicModelConstants;
import lombok.*;
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

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO {
        /**
         * 抽奖奖品ID - 内部流转使用
         */
        private Integer awardId;
        /**
         *
         */
        private String logicModel;
        /**
         * 抽奖奖品规则
         */
        private String awardRuleValue;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        RULE_WHITELIST("rule_whitelist", "白名单抽奖"),
        ;

        private final String code;
        private final String info;

    }

}
