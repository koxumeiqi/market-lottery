package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouter;
import com.ly.infrastructure.persistent.po.RaffleActivityAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author myz03
* @description 针对表【raffle_activity_account(抽奖活动账户表)】的数据库操作Mapper
* @createDate 2024-04-25 12:43:44
* @Entity com.ly.infrastructure.persistent.po.RaffleActivityAccount
*/
@Mapper
public interface RaffleActivityAccountMapper extends BaseMapper<RaffleActivityAccount> {

    int updateAccountQuota(RaffleActivityAccount raffleActivityAccount);

    @DBRouter
    RaffleActivityAccount queryActivityAccountByUserId(RaffleActivityAccount raffleActivityAccountReq);

    int updateActivityAccountSubtractionQuota(RaffleActivityAccount build);

    void updateActivityAccountMonthSurplusImageQuota(RaffleActivityAccount build);

    void updateActivityAccountDaySurplusImageQuota(RaffleActivityAccount build);

}




