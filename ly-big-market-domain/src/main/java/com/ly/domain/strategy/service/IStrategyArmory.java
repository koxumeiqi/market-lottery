package com.ly.domain.strategy.service;

/**
 * 策略装配库，负责初始化策略计算的，将策略存储进Redis中后续使用
 */
public interface IStrategyArmory {

    /**
     * 初始化策略表
     * @param strategyId
     */
    boolean assembleLotteryStrategy(Long strategyId);

}
