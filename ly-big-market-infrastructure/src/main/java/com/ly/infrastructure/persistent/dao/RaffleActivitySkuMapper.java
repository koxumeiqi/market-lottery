package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RaffleActivitySku;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author myz03
* @description 针对表【raffle_activity_sku】的数据库操作Mapper
* @createDate 2024-04-25 12:43:44
* @Entity com.ly.infrastructure.persistent.po.RaffleActivitySku
*/
@Mapper
public interface RaffleActivitySkuMapper extends BaseMapper<RaffleActivitySku> {

    void updateActivitySkuStock(Long sku);

    void clearActivitySkuStock(Long sku);

    List<RaffleActivitySku> queryActivitySkuListByActivityId(Long activityId);

}




