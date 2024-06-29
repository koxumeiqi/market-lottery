package com.ly.domain.strategy.service.raffle;


import com.ly.domain.strategy.model.valobj.RuleWeightVO;

import java.util.List;
import java.util.Map;

/**
 * 抽奖规则接口：提供对规则的业务功能查询
 */
public interface IRaffleRule {

    /**
     * 根据规则树ID集合奖品中加锁数量的配置【部分奖品需要抽奖N次后才能解锁】
     * @param treeIds
     * @return
     */
    Map<String,Integer> queryAwardRuleLockCount(String[] treeIds);

    /**
     * 查询奖品权重配置
     *
     * @param strategyId 策略ID
     * @return 权重规则
     */
    List<RuleWeightVO> queryAwardRuleWeight(Long strategyId);


    /**
     * 查询奖品权重配置
     *
     * @param activityId 活动ID
     * @return 权重规则
     */
    List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId);

}
