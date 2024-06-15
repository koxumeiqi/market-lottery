package com.ly.trigger.service;

import com.ly.api.IRaffleService;
import com.ly.api.dto.*;
import com.ly.domain.strategy.model.entity.RaffleAwardEntity;
import com.ly.domain.strategy.model.entity.RaffleFactorEntity;
import com.ly.domain.strategy.model.entity.StrategyAwardEntity;
import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.service.armory.IStrategyArmory;
import com.ly.domain.strategy.service.armory.IStrategyFetch;
import com.ly.domain.strategy.service.raffle.IRaffleAward;
import com.ly.domain.strategy.service.raffle.IRaffleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//@DubboService
@Service
public class RaffleService implements IRaffleService {

    @Autowired
    private IStrategyArmory strategyArmory;

    @Autowired
    private IRaffleAward raffleAward;

    @Autowired
    private IRaffleStrategy raffleStrategy;

    @Autowired
    private IStrategyFetch strategyFetch;

    @Override
    public boolean strategyArmory(Long strategyId) {
        return strategyArmory.assembleLotteryStrategy(strategyId);
    }

    @Override
    public List<RaffleAwardListResponseDTO> queryRaffleAwardList(RaffleAwardListRequestDTO requestDTO) {
        /*// 查询奖品配置
        List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleStrategyAwardList(requestDTO.getStrategyId());
        List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOS = new ArrayList<>(strategyAwardEntities.size());
        for (StrategyAwardEntity strategyAward : strategyAwardEntities) {
            raffleAwardListResponseDTOS.add(RaffleAwardListResponseDTO.builder()
                    .awardId(strategyAward.getAwardId())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubtitle(strategyAward.getAwardSubtitle())
                    .sort(strategyAward.getSort())
                    .build());
        }
        return raffleAwardListResponseDTOS;*/
        return null;
    }

    @Override
    public RaffleResponseDTO randomRaffle(RaffleRequestDTO requestDTO) {
        // 调用抽奖接口
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(RaffleFactorEntity.builder()
                .userId("system")
                .strategyId(requestDTO.getStrategyId())
                .build());
        return RaffleResponseDTO.builder()
                .awardId(raffleAwardEntity.getAwardId())
                .awardIndex(raffleAwardEntity.getSort())
                .build();
    }

    @Override
    public List<StrategyResponseDTO> queryStrategyList() {
        List<StrategyEntity> strategyEntities = strategyFetch.fetchStrategy();
        List<StrategyResponseDTO> dtos = new ArrayList<>();
        strategyEntities.forEach(strategyEntity -> {
            dtos.add(StrategyResponseDTO.builder()
                    .strategyId(strategyEntity.getStrategyId())
                    .strategyDesc(strategyEntity.getStrategyDesc())
                    .build());
        });
        return dtos;
    }
}
