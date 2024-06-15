package com.ly.trigger.listener;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendAwardComsumer {

    @Value("${spring.kafka.topic.send_award}")
    private String topic;

    @KafkaListener(topics = "${spring.kafka.topic.send_award}")
    public void sendAward(String message, Acknowledgment ack) {
        try {
            log.info("监听用户奖品发送消息 topic: {} message: {}", topic, message);
        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        } finally {
            ack.acknowledge();
        }
    }

}
