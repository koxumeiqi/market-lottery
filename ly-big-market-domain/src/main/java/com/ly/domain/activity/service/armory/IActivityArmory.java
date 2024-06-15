package com.ly.domain.activity.service.armory;


/**
 * 活动库存填充
 */
public interface IActivityArmory {

    boolean assembleActivitySku(Long sku);

    void assembleActivitySkuByActivityId(Long activityId);

}
