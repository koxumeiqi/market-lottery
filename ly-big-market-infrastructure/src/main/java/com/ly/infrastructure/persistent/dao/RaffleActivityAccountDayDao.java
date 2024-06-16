package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.infrastructure.persistent.po.RaffleActivityAccountDay;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RaffleActivityAccountDayDao extends BaseMapper<RaffleActivityAccountDay> {

    @DBRouter
    RaffleActivityAccountDay queryActivityAccountDayByUserId(RaffleActivityAccountDay raffleActivityAccountDayReq);

    int updateActivityAccountDaySubtractionQuota(RaffleActivityAccountDay build);

    void insertActivityAccountDay(RaffleActivityAccountDay build);

    @DBRouter
    Integer queryRaffleActivityAccountDayPartakeCount(RaffleActivityAccountDay raffleActivityAccountDay);

    void addAccountQuota(RaffleActivityAccountDay raffleActivityAccountDay);

}
