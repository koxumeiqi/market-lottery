package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.ly.infrastructure.persistent.po.UserCreditOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DBRouterStrategy(isSplitTable = true)
public interface IUserCreditOrderDao {

    void insert(UserCreditOrder userCreditOrder);


}
