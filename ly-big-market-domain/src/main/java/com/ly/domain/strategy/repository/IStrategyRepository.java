package com.ly.domain.strategy.repository;

import com.ly.domain.strategy.model.entity.StrategyAwardEntity;
import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.model.entity.StrategyRuleEntity;

import java.util.List;
import java.util.Map;

public interface IStrategyRepository {

    /**
     * 查询对应的策略奖品信息
     *
     * @param strategyId
     * @return
     */
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    /**
     * 将乱序的查找表格存储到redis
     *
     * @param key
     * @param rateRange
     * @param shuffleStrategyAwardSearchRateTable
     */
    void storeStrategyAwardSearchRateTable(String key, int rateRange,
                                           Map<Integer, Integer> shuffleStrategyAwardSearchRateTable);


    int getRateRange(Long strategyId);

    int getRateRange(String key);

    Integer getStrategyAwardAssemble(String key, int i);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight);
}
