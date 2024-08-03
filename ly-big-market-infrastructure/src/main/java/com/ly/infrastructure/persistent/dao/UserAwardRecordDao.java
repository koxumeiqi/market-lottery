package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.infrastructure.persistent.po.UserAwardRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAwardRecordDao extends BaseMapper<UserAwardRecord> {

    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

    int updateAwardRecordCompletedState(UserAwardRecord userAwardRecordReq);

    List<UserAwardRecord> queryAwardListWithActivityId(Long activityId);

}
