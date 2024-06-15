package com.ly.domain.activity.model.entity;

import lombok.Data;

/**
 * 活动重置实体
 */
@Data
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

}
