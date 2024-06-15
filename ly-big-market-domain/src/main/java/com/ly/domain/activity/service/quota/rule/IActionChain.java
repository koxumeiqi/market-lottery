package com.ly.domain.activity.service.quota.rule;

import com.ly.domain.activity.model.entity.ActivityCountEntity;
import com.ly.domain.activity.model.entity.ActivityEntity;
import com.ly.domain.activity.model.entity.ActivitySkuEntity;

public interface IActionChain extends IActionChainArmory {

    boolean action(ActivitySkuEntity activitySkuEntity, ActivityEntity activityEntity, ActivityCountEntity activityCountEntity);

}
