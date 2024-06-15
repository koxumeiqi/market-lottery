package com.ly.test.infrastructure;


import com.ly.infrastructure.persistent.redis.RedissonService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisTest {

    @Autowired
    private RedissonService redissonService;

    @Test
    public void test() {
        RMap<Object, Object> map = redissonService.getMap("strategy_test");
        map.put(7, 101);
        map.put(6, 101);
        map.put(1, 101);
        map.put(0, 101);
        map.put(5, 101);
        map.put(2, 101);
        map.put(3, 101);
        map.put(4, 101);
        map.put(8, 102);
        map.put(9, 102);
        map.put(10, 102);
        log.info("测试结果：{}", redissonService.getFromMap("strategy_test",
                (int) Math.random() % 11).toString());
    }

}
