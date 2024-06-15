package com.ly.test.domain.rebate;


import com.alibaba.fastjson.JSON;
import com.ly.domain.rebate.model.entity.BehaviorEntity;
import com.ly.domain.rebate.model.vo.BehaviorTypeVO;
import com.ly.domain.rebate.service.IBehaviorRebateService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RebateApiTest {

    @Resource
    private IBehaviorRebateService behaviorRebateService;

    @Test
    public void test_create_order() {
        BehaviorEntity behaviorEntity = new BehaviorEntity();
        behaviorEntity.setUserId("myz");
        behaviorEntity.setOutBusinessNo("20240614");
        behaviorEntity.setBehaviorTypeVO(BehaviorTypeVO.SIGN);
        List<String> orderIds = behaviorRebateService.createOrder(behaviorEntity);
        log.info("测试数据：{}", JSON.toJSONString(orderIds));
    }

}
