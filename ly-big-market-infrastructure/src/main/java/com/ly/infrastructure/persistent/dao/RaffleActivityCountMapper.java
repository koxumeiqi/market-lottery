package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RaffleActivityCount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author myz03
* @description 针对表【raffle_activity_count(抽奖活动次数配置表)】的数据库操作Mapper
* @createDate 2024-04-25 12:43:44
* @Entity com.ly.infrastructure.persistent.po.RaffleActivityCount
*/
@Mapper
public interface RaffleActivityCountMapper extends BaseMapper<RaffleActivityCount> {

    RaffleActivityCount queryRaffleActivityCountByActivityCountId(Long activityCountId);

}




