package com.ly.domain.rebate.repository;

import com.ly.domain.rebate.aggregate.BehaviorRebateAggregate;
import com.ly.domain.rebate.model.vo.BehaviorTypeVO;
import com.ly.domain.rebate.model.vo.DailyBehaviorRebateVO;

import java.util.List;

public interface IBehaviorRebateRepository {

    List<DailyBehaviorRebateVO> queryDailyBehaviorRebateConfig(BehaviorTypeVO behaviorTypeVO);

    void saveUserRebateRecord(String userId, List<BehaviorRebateAggregate> behaviorRebateAggregates);

}
