package com.ly.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 未完成支付的活动单
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnpaidActivityOrderEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 外部业务唯一ID
     */
    private String outBusinessNo;

    /**
     * 订单金额
     */
    private BigDecimal payAmount;

}
