package com.ly.test.domain;


import com.alibaba.fastjson.JSON;
import com.ly.domain.strategy.model.entity.RaffleAwardEntity;
import com.ly.domain.strategy.model.entity.RaffleFactorEntity;
import com.ly.domain.strategy.service.raffle.DefaultRaffleStrategy;
import com.ly.domain.strategy.service.rule.chain.ILogicChain;
import com.ly.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.ly.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleStrategyTest {

    @Autowired
    private DefaultRaffleStrategy raffleStrategy;

    @Autowired
    private RuleWeightLogicChain ruleWeightLogicChain;

    @Autowired
    private DefaultChainFactory chainFactory;

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(ruleWeightLogicChain,
                "userScore", 6000L);
    }

    @Test
    public void test_performRaffle() {
        /*RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user11")
                .strategyId(100001L)
                .build();*/
        ILogicChain logicChain = chainFactory.openLogicChain(100001L);
        logicChain.logic("user11", 100001L);
//        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
//        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
//        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

    @Test
    public void test_performRaffleWhitelist() {
        /*RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user2")
                .strategyId(100001L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));*/
        ILogicChain logicChain = chainFactory.openLogicChain(100001L);
        logicChain.logic("user2", 100001L);
    }

    @Test
    public void test_performRaffle_blacklist() {
        /*RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("user1")  // 黑名单用户 user001,user002,user003
                .strategyId(100001L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));*/
        ILogicChain logicChain = chainFactory.openLogicChain(100001L);
        logicChain.logic("user1", 100001L);

    }

}
