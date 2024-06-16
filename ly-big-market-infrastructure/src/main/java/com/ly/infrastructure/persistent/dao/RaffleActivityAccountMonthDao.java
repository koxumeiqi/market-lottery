package com.ly.infrastructure.persistent.dao;


import cn.xc.custom.db.router.annotation.DBRouter;
import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.infrastructure.persistent.po.RaffleActivityAccountMonth;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityAccountMonthDao extends BaseMapper<RaffleActivityAccountMonth> {

    @DBRouter
    RaffleActivityAccountMonth queryActivityAccountMonthByUserId(RaffleActivityAccountMonth raffleActivityAccountMonthReq);

    int updateActivityAccountMonthSubtractionQuota(RaffleActivityAccountMonth build);

    void insertActivityAccountMonth(RaffleActivityAccountMonth build);

    void addAccountQuota(RaffleActivityAccountMonth raffleActivityAccountMonth);

}
