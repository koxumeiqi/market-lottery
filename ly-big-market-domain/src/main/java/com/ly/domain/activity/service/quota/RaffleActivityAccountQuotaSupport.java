package com.ly.domain.activity.service.quota;

import com.ly.domain.activity.model.entity.ActivityCountEntity;
import com.ly.domain.activity.model.entity.ActivityEntity;
import com.ly.domain.activity.model.entity.ActivitySkuEntity;
import com.ly.domain.activity.repository.IActivityRepository;
import com.ly.domain.activity.service.quota.rule.IActionChain;
import com.ly.domain.activity.service.quota.rule.factory.ActionChainFactory;

public class RaffleActivityAccountQuotaSupport {

    protected final ActionChainFactory actionChainFactory;

    protected final IActivityRepository repository;

    public RaffleActivityAccountQuotaSupport(ActionChainFactory actionChainFactory, IActivityRepository repository) {
        this.actionChainFactory = actionChainFactory;
        this.repository = repository;
    }

    public boolean checkRule(ActivitySkuEntity skuEntity,
                             ActivityEntity activityEntity,
                             ActivityCountEntity countEntity) {
        IActionChain actionChain = actionChainFactory.getActionChain();
        return actionChain.action(skuEntity, activityEntity, countEntity);
    }

    public ActivitySkuEntity queryActivitySku(Long sku){
        return repository.queryActivitySku(sku);
    }

    public ActivityEntity queryRaffleActivityByActivityId(Long activityId){
        return repository.queryRaffleActivityByActivityId(activityId);
    }

    public ActivityCountEntity queryRaffleActivityCountByActivityCountId(Long activityCountId){
        return repository.queryRaffleActivityCountByActivityCountId(activityCountId);
    }
}
