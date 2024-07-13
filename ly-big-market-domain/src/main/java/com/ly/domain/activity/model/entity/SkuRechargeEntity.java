package com.ly.domain.activity.model.entity;

import com.ly.domain.activity.model.vo.OrderTradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动重置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkuRechargeEntity {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 商品SKU - activity + activity count
     */
    private Long sku;

    /**
     * 外部业务单号 - 幂等 - 外部传递，防不会多次充值
     */
    private String outBusinessNo;

    /** 订单交易类型；支付类型 or 不支付 */
    private OrderTradeTypeVO orderTradeType = OrderTradeTypeVO.rebate_no_pay_trade;

}
