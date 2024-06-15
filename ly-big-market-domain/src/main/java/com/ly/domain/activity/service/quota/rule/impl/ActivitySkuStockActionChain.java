package com.ly.domain.activity.service.quota.rule.impl;

import com.ly.domain.activity.model.entity.ActivityCountEntity;
import com.ly.domain.activity.model.entity.ActivityEntity;
import com.ly.domain.activity.model.entity.ActivitySkuEntity;
import com.ly.domain.activity.model.vo.ActivitySkuStockKeyVO;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.domain.activity.service.armory.IActivityDispatch;
import com.ly.domain.activity.service.quota.rule.AbstractActionChain;
import com.ly.types.enums.ResponseCode;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component("activity_sku_stock_action")
@Slf4j
public class ActivitySkuStockActionChain extends AbstractActionChain {

    @Autowired
    private IActivityDispatch activityDispatch;

    @Autowired
    private IActivityRepository activityRepository;

    @Override
    public boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】开始。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
        // 扣减库存
        boolean status = activityDispatch.subtractionActivitySkuStock(activitySkuEntity.getSku(), activityEntity.getEndDateTime());
        // true：扣减成功
        // false：没库存 or 扣减失败
        if (status) {
            log.info("活动责任链-商品库存处理【有效期、状态、库存(sku)】成功。sku:{} activityId:{}", activitySkuEntity.getSku(), activityEntity.getActivityId());
            // 写入延迟队列，延迟消费更新库存记录
            activityRepository.activitySkuStockConsumeSendQueue(ActivitySkuStockKeyVO.builder()
                    .sku(activitySkuEntity.getSku())
                    .activityId(activityEntity.getActivityId())
                    .build());
            return true;
        }
        throw new AppException(ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getCode(), ResponseCode.ACTIVITY_SKU_STOCK_ERROR.getInfo());
    }

}
