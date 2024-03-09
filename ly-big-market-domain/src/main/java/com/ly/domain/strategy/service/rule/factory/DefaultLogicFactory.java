package com.ly.domain.strategy.service.rule.factory;

import com.alibaba.fastjson2.util.AnnotationUtils;
import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.service.annotation.LogicStrategy;
import com.ly.domain.strategy.service.rule.ILogicFilter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DefaultLogicFactory {

    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilters) {
        logicFilters.forEach(
                logic -> {
                    LogicStrategy strategy = AnnotationUtils.findAnnotation(logic.getClass(),
                            LogicStrategy.class);
                    if (strategy != null) {
                        logicFilterMap.put(strategy.logicModel().getCode(),
                                logic);
                    }
                }
        );
    }

    public <T extends RuleActionEntity.RaffleEntity> Map<String, ILogicFilter<T>> openLogicFilter() {
        return (Map<String, ILogicFilter<T>>) (Map<?, ?>) logicFilterMap;
    }

    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_DEFAULT("rule_default", "默认抽奖"),
        RULE_BLACKLIST("rule_blacklist", "黑名单抽奖"),
        RULE_WHITELIST("rule_whitelist", "白名单抽奖"),
        RULE_WEIGHT("rule_weight", "权重规则"),
        ;

        private final String code;
        private final String info;

    }
}
