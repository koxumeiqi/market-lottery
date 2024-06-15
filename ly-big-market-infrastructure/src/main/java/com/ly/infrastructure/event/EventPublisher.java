package com.ly.infrastructure.event;


import com.alibaba.fastjson.JSON;
import com.ly.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发送
 */
@Component
@Slf4j
public class EventPublisher {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    public void publish(String topic, String messageJson) {
        try {
            kafkaTemplate.send(topic, messageJson);
            log.info("发送消息成功, topic:{}, message:{}", topic, messageJson);
        } catch (Exception e) {
            log.error("发送消息失败, topic:{}, message:{}", topic, messageJson, e);
            throw e;
        }
    }

}
