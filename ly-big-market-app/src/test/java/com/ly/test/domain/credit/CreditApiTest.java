package com.ly.test.domain.credit;

import com.ly.domain.credit.model.entity.TradeEntity;
import com.ly.domain.credit.model.valobj.TradeNameVO;
import com.ly.domain.credit.model.valobj.TradeTypeVO;
import com.ly.domain.credit.service.ICreditAdjustService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class CreditApiTest {

    @Resource
    private ICreditAdjustService creditAdjustService;

    @Test
    public void test_pay_recharge_credit_success() throws InterruptedException {
        String orderId = creditAdjustService.createOrder(
                TradeEntity.builder()
                        .userId("myz")
                        .outBusinessNo("297028306233")
                        .tradeType(TradeTypeVO.REVERSE)
                        .tradeName(TradeNameVO.CONVERT_SKU)
                        .amount(new BigDecimal(-9.9)).build());
        log.info("orderId:{}", orderId);
        new CountDownLatch(1).await();
    }

}
