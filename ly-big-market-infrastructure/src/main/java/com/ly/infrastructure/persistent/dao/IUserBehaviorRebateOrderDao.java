package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.ly.infrastructure.persistent.po.DailyBehaviorRebate;
import com.ly.infrastructure.persistent.po.UserBehaviorRebateOrder;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户行为返利流水订单表
 * @create 2024-04-30 13:48
 */
@Mapper
@DBRouterStrategy(isSplitTable = true)
public interface IUserBehaviorRebateOrderDao {

    void insert(UserBehaviorRebateOrder userBehaviorRebateOrder);

    @DBRouter
    List<UserBehaviorRebateOrder> queryOrderByOutBusinessNo(UserBehaviorRebateOrder userBehaviorRebateOrderReq);

}
