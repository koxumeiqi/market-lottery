package com.ly.domain.strategy.model.entity;


import lombok.Data;

/**
 * 规则物料实体对象，用于过滤规则的必要参数信息
 */
@Data
public class RuleMatterEntity {

    /**
     * 用户ID
     */
    private String userId;
    /** 抽奖策略ID */
    private Long strategyId;
    /** 抽奖奖品ID - 内部流转使用 */
    private Integer awardId;
    /**
     * 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】
     */
    private String ruleModel;

}
