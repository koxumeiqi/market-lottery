package com.ly.domain.strategy.service.raffle;


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

}
