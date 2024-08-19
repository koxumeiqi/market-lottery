package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.infrastructure.persistent.po.UserRaffleOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DBRouterStrategy(isSplitTable = true)
public interface UserRaffleOrderDao extends BaseMapper<UserRaffleOrder> {

    @DBRouter
    UserRaffleOrder queryNoUsedRaffleOrder(UserRaffleOrder userRaffleOrderReq);

    int updateUserRaffleOrderStateUsed(UserRaffleOrder userRaffleOrderReq);

    @DBRouter
    UserRaffleOrder queryByOrderId(UserRaffleOrder userRaffleOrderReq);

}
