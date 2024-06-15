package com.ly.infrastructure.persistent.dao;

import cn.xc.custom.db.router.annotation.DBRouterStrategy;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.infrastructure.persistent.po.UserAwardRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DBRouterStrategy(isSplitTable = true)
public interface UserAwardRecordDao extends BaseMapper<UserAwardRecord> {

    void insertUserAwardRecord(UserAwardRecord userAwardRecord);

}
