package com.ly.api;

import com.ly.api.dto.*;
import com.ly.types.model.Response;

import java.util.List;

public interface IRaffleService {

    /**
     * 策略装配接口
     *
     * @param strategyId
     * @return
     */
    boolean strategyArmory(Long strategyId);


    /**
     * 查询抽奖奖品列表配置
     *
     * @param requestDTO 抽奖奖品列表查询请求参数
     * @return 奖品列表数据
     */
    List<RaffleAwardListResponseDTO> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO);

    RaffleResponseDTO randomRaffle(RaffleRequestDTO requestDTO);

    List<StrategyResponseDTO> queryStrategyList();

}
