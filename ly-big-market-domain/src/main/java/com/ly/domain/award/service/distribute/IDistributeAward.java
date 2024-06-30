package com.ly.domain.award.service.distribute;


import com.ly.domain.award.model.entity.DistributeAwardEntity;

/**
 * @description 分发奖品接口
 */
public interface IDistributeAward {

    void giveOutPrizes(DistributeAwardEntity distributeAwardEntity);

}
