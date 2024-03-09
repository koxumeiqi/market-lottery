package com.ly.domain.strategy.model.entity;


import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 规则动作实体
 * @param <T>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity<T extends RuleActionEntity.RaffleEntity> {

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    static public class RaffleEntity {
    }

    /**
     * 抽奖之前
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class BeforeRaffleEntity extends RaffleEntity {
        private Long strategyId;
        /**
         * 权重值key：用于抽奖时可以选择权重抽奖
         */
        private String ruleWeightValueKey;

        private Integer awardId;
    }

    static public class AfterRaffleEntity extends RaffleEntity {

    }


}
