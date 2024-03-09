package com.ly.domain.strategy.service.rule.impl;

import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.model.entity.RuleMatterEntity;
import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.annotation.LogicStrategy;
import com.ly.domain.strategy.service.rule.ILogicFilter;
import com.ly.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.ly.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.BeforeRaffleEntity> {

    @Resource
    private IStrategyRepository repository;

    public Long userScore = 5500L; // TODO 用户分值，后续是由全端传递的，这里是先模拟

    @Override
    public RuleActionEntity<RuleActionEntity.BeforeRaffleEntity> filter(RuleMatterEntity ruleMatterEntity) {

        log.info("规则过滤-权重范围 userId:{} strategyId:{} ruleModel:{}", ruleMatterEntity.getUserId(), ruleMatterEntity.getStrategyId(), ruleMatterEntity.getRuleModel());

        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        // 1. 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {
            return RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys, Comparator.reverseOrder());
        // 找出最小符合的值
        Long nextValue = analyticalSortedKeys.stream()
                .filter(key -> userScore >= key)
                .findFirst()
                .orElse(null);

        if (null != nextValue) {
            return RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                    .data(RuleActionEntity.BeforeRaffleEntity.builder()
                            .strategyId(strategyId)
                            .ruleWeightValueKey(nextValue.toString())
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }

        return RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
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