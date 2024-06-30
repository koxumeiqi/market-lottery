package com.ly.test.domain;


import com.ly.domain.strategy.service.armory.StrategyArmoryDispatch;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@Slf4j
@RunWith(SpringRunner.class)
public class StrategyArmoryTest {

    @Autowired
    private StrategyArmoryDispatch strategyArmory;

    @Test
    public void test() {
        strategyArmory.assembleLotteryStrategy(100006L);
    }

    @Test
    public void testRandomAward() {
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
        log.info("抽奖结果:{}", strategyArmory.getRandomAwardId(100001L));
    }

    @Test
    public void testRandomAwardWithRuleWeight() {
        log.info("抽奖结果4000:{}", strategyArmory.getRandomAwardId(100001L, String.valueOf(4000)));
        log.info("抽奖结果4000:{}", strategyArmory.getRandomAwardId(100001L, String.valueOf(4000)));
        log.info("抽奖结果6000:{}", strategyArmory.getRandomAwardId(100001L, "6000"));
        log.info("抽奖结果6000:{}", strategyArmory.getRandomAwardId(100001L, "6000"));
    }


}
