package com.ly.infrastructure.persistent.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.infrastructure.persistent.po.StrategyAward;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IStrategyAwardDao extends BaseMapper<StrategyAward> {
    void updateStrategyAwardStock(StrategyAward strategyAward);
}
