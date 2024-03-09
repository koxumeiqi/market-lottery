package com.ly.domain.strategy.service.rule.impl;

import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.model.entity.RuleMatterEntity;
import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.annotation.LogicStrategy;
import com.ly.domain.strategy.service.rule.ILogicFilter;
import com.ly.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.ly.types.common.Constants;
import com.sun.xml.internal.ws.db.glassfish.RawAccessorWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Component
@LogicStrategy(logicModel = DefaultLogicFactory.LogicModel.RULE_WHITELIST)
@Slf4j
public class RuleWhiteListLogicFilter implements ILogicFilter<RuleActionEntity.BeforeRaffleEntity> {

    @Autowired
    private IStrategyRepository repository;

    @Override
    public RuleActionEntity<RuleActionEntity.BeforeRaffleEntity> filter(RuleMatterEntity ruleMatterEntity) {

        log.info("规则过滤-白名单 userId:{} strategyId:{} ruleModel:{}",
                ruleMatterEntity.getUserId(),
                ruleMatterEntity.getStrategyId(),
                ruleMatterEntity.getRuleModel());

        // 获取白名单的规则
        Long strategyId = ruleMatterEntity.getStrategyId();
        String userId = ruleMatterEntity.getUserId();
        Integer awardId = ruleMatterEntity.getAwardId();
        // 获取到白名单参与的用户 user1,user2,user3
        String ruleValue =
                repository.queryStrategyWhiteRuleValue(strategyId, ruleMatterEntity.getRuleModel());

        // 没有参与的白名单用户的话就走默认抽取逻辑
        if (StringUtils.isEmpty(ruleValue)) {
            return RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        String[] userIds = ruleValue.split(Constants.SPLIT);
        Set<String> whiteUserIds = new HashSet<>(Arrays.asList(userIds));
        // 如果用户在白名单中的话就去查ruleWeight，取最高的
        if (whiteUserIds.contains(userId)) {
            String maxValue = findMaxWeight(strategyId);
            if (maxValue != null) {// 不等于null说明RuleWeight是有规则的，取最大的让白名单抽取
                RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .data(
                                RuleActionEntity.BeforeRaffleEntity.builder()
                                        .ruleWeightValueKey(maxValue)
                                        .strategyId(strategyId)
                                        .build()
                        )
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_WHITELIST.getCode())
                        .build();
            }
        }
        return RuleActionEntity.<RuleActionEntity.BeforeRaffleEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    private String findMaxWeight(Long strategyId) {
        String ruleValue = repository.queryStrategyRuleValue(strategyId,
                null, DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode());

        // 1. 根据用户ID查询用户抽奖消耗的积分值，本章节我们先写死为固定的值。后续需要从数据库中查询。
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (null == analyticalValueGroup || analyticalValueGroup.isEmpty()) {
            return null;
        }

        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Long maxValue = analyticalSortedKeys.stream()
                .max(Long::compare)
                .orElse(null);
        return maxValue != null ? maxValue.toString() : null;
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
