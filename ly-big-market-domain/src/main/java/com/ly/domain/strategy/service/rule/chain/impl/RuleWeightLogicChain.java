package com.ly.domain.strategy.service.rule.chain.impl;


import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.armory.IStrategyDispatch;
import com.ly.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.ly.types.common.Constants;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component(LogicModelConstants.LOGIC_WEIGHT)
@Slf4j
public class RuleWeightLogicChain extends AbstractLogicChain {

    @Autowired
    private IStrategyRepository repository;

    @Autowired
    private IStrategyDispatch strategyDispatch;

    // 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
    public Long userScore = 0L; // TODO 用户分值，后续是由全端传递的，这里是先模拟

    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-权重开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());

        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        // 1. 解析权重规则值 4000:102,103,104,105 拆解为；4000 -> 4000:102,103,104,105 便于比对判断
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {
            log.warn("抽奖责任链-权重告警【策略配置权重，但ruleValue未配置相应值】 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
            return next().logic(userId, strategyId);
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        // 3. 找出最小符合的值，也就是【4500 积分，能找到 4000:102,103,104,105】、【5000 积分，能找到 5000:102,103,104,105,106,107】
        Long nextValue = analyticalSortedKeys.stream()
                .sorted(Comparator.reverseOrder())
                .filter(analyticalSortedKeyValue -> userScore >= analyticalSortedKeyValue)
                .findFirst()
                .orElse(null);

        // 4. 权重抽奖
        if (null != nextValue) {
            Integer awardId = strategyDispatch.getRandomAwardId(strategyId, nextValue.toString());
            log.info("抽奖责任链-权重接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
            return awardId;
        }

        // 5. 过滤其他责任链
        log.info("抽奖责任链-权重放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return LogicModelConstants.LOGIC_WEIGHT;
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            // 检查输入是否为空
            if (ruleValueKey == null || ruleValueKey.isEmpty()) {
                return ruleValueMap;
            }
            // 分割字符串以获取键和值
            String[] parts = ruleValueKey.split(Constants.COLON);
            if (parts.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseLong(parts[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
}
