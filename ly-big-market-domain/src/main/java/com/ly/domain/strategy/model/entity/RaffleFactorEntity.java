package com.ly.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖因子实体
 * @create 2024-01-06 09:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {

    /**
     * 用户ID
     */
    private String userId;
    /**
     * 订单id，串整个链路，后面用它定位活动id和抽奖id
     */
    private String orderId;
    /**
     * 策略ID
     */
    private Long strategyId;
    /**
     * 结束时间
     */
    private Date endDateTime;

}
