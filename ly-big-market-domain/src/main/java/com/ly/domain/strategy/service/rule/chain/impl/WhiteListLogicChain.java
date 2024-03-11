package com.ly.domain.strategy.service.rule.chain.impl;

import com.ly.domain.strategy.model.entity.RuleActionEntity;
import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.armory.IStrategyDispatch;
import com.ly.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.ly.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.ly.types.common.Constants;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;


@Component(LogicModelConstants.LOGIC_WHITE_LIST)
@Slf4j
public class WhiteListLogicChain extends AbstractLogicChain {

    @Autowired
    private IStrategyRepository repository;

    @Autowired
    private IStrategyDispatch strategyDispatch;

    @Override
    public Integer logic(String userId, Long strategyId) {
        log.info("抽奖责任链-白名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId,
                ruleModel());

        // 没有参与的白名单用户的话就走默认抽取逻辑
        if (StringUtils.isEmpty(ruleValue)) {
            log.info("抽奖责任链-白名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
            return next().logic(userId, strategyId);
        }
        String[] userIds = ruleValue.split(Constants.SPLIT);
        Set<String> whiteUserIds = new HashSet<>(Arrays.asList(userIds));
        // 如果用户在白名单中的话就去查ruleWeight，取最高的
        if (whiteUserIds.contains(userId)) {
            String maxValue = findMaxWeight(strategyId);
            if (maxValue != null) {// 不等于null说明RuleWeight是有规则的，取最大的让白名单抽取
                Integer awardId = strategyDispatch.getRandomAwardId(strategyId, maxValue);
                log.info("抽奖责任链-白名单接管 userId: {} strategyId: {} awardId: {}", userId, strategyId, awardId);
                return awardId;
            }
        }
        log.info("抽奖责任链-白名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }

    @Override
    protected String ruleModel() {
        return LogicModelConstants.LOGIC_WHITE_LIST;
    }

    private String findMaxWeight(Long strategyId) {
        String ruleValue = repository.queryStrategyRuleValue(strategyId, DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode());

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

