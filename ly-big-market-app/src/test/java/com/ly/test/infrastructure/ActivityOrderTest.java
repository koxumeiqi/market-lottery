package com.ly.test.infrastructure;


import com.ly.domain.activity.model.entity.ActivityShopCartEntity;
import com.ly.domain.activity.service.IRaffleActivityAccountQuotaService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class ActivityOrderTest {

    @Autowired
    private IRaffleActivityAccountQuotaService raffleOrder;

    @Test
    public void test() {
        ActivityShopCartEntity shopCartEntity = ActivityShopCartEntity.builder()
                .sku(9011L)
                .build();
//        raffleOrder.createRaffleActivityOrder(shopCartEntity);
    }

}
