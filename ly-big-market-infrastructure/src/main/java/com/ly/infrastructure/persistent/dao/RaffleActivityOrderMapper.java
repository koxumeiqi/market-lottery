package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.ly.infrastructure.persistent.po.RaffleActivityOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author myz03
* @description 针对表【raffle_activity_order(抽奖活动单)】的数据库操作Mapper
* @createDate 2024-04-25 12:43:44
* @Entity com.ly.infrastructure.persistent.po.RaffleActivityOrder
*/
@Mapper
@DBRouterStrategy(isSplitTable = true)
public interface RaffleActivityOrderMapper extends BaseMapper<RaffleActivityOrder> {

    int insert(RaffleActivityOrder raffleActivityOrder);

    @DBRouter
    List<RaffleActivityOrder> queryRaffleActivityOrderByUserId(String userId);

}



