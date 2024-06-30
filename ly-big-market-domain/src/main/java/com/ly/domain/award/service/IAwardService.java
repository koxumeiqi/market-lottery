package com.ly.domain.award.service;

import com.ly.domain.award.model.entity.DistributeAwardEntity;
import com.ly.domain.award.model.entity.UserAwardRecordEntity;

/**
 * 奖品服务接口
 */
public interface IAwardService {

    void saveUserAwardRecord(UserAwardRecordEntity userAwardRecordEntity);

    /**
     * 配送发货奖品
     */
    void distributeAward(DistributeAwardEntity distributeAwardEntity);

}
