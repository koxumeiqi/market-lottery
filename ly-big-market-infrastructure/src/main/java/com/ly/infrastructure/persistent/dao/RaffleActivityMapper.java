package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RaffleActivity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author myz03
* @description 针对表【raffle_activity(抽奖活动表)】的数据库操作Mapper
* @createDate 2024-04-25 12:43:44
* @Entity com.ly.infrastructure.persistent.po.RaffleActivity
*/
@Mapper
public interface RaffleActivityMapper extends BaseMapper<RaffleActivity> {

    Long queryStrategyIdByActivityId(Long activityId);

    Long queryActivityIdByStrategyId(Long strategyId);

    List<RaffleActivity> queryActivityList();

}




