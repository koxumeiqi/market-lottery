package com.ly.trigger.listener;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ly.domain.activity.service.IRaffleActivitySkuStockService;
import com.ly.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ActivitySkuStockZeroComsumer {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String topic;

    @Autowired
    private IRaffleActivitySkuStockService skuStock;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.activity_sku_stock_zero}"))
    public void onMessage(String message) {
        try {
            log.info("监听活动sku库存消耗为0消息 topic: {} message: {}", topic, message);
            // 转换对象
            BaseEvent.EventMessage<Long> eventMessage = JSON.parseObject(message,
                    new TypeReference<BaseEvent.EventMessage<Long>>() {
                    }.getType());
            Long sku = eventMessage.getData();
            // 更新库存
            skuStock.clearActivitySkuStock(sku);
            // 清空队列
            skuStock.clearQueueValue();
        } catch (Exception e) {
            log.error("监听活动sku库存消耗为0消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        } finally {
//            acknowledgment.acknowledge();
        }

    }

}
