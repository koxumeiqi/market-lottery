package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.DailyBehaviorRebate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @description 日常行为返利活动配置
 * @create 2024-04-30 13:48
 */
@Mapper
public interface IDailyBehaviorRebateDao {

    /**
     * 创建行为动作的入账订单
     *
     * @param behaviorType
     * @return
     */
    List<DailyBehaviorRebate> queryDailyBehaviorRebateByBehaviorType(String behaviorType);

}
