package com.ly.domain.activity.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出货单据实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryOrderEntity {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 业务防重ID - 外部透传。返利、行为等唯一标识
     */
    private String outBusinessNo;

}
