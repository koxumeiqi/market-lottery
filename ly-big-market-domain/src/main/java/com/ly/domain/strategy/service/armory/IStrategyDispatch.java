package com.ly.domain.strategy.service.armory;

public interface IStrategyDispatch {

    /**
     * 通过策略id随机抽取奖品
     *
     * @param strategyId
     * @return
     */
    Integer getRandomAwardId(Long strategyId);

    /**
     * 获取抽奖策略装配的随机结果，带权重值的
     * @param strategyId
     * @param ruleWeightValue
     * @return
     */
    Integer getRandomAwardId(Long strategyId, String ruleWeightValue);


}
