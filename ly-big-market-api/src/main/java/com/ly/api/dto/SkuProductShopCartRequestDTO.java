package com.ly.api.dto;


import lombok.Data;

/**
 * sku 商品购物车实体对象
 */
@Data
public class SkuProductShopCartRequestDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * sku 商品
     */
    private Long sku;

}
