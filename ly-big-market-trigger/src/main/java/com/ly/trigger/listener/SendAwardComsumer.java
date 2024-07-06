package com.ly.trigger.listener;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ly.domain.award.event.SendAwardMessageEvent;
import com.ly.domain.award.model.entity.DistributeAwardEntity;
import com.ly.domain.award.service.IAwardService;
import com.ly.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class SendAwardComsumer {

    @Value("${spring.kafka.topic.send_award}")
    private String topic;

    @Resource
    private IAwardService awardService;

    @KafkaListener(topics = {"send_award"})
    public void sendAward(String message, Acknowledgment ack) {
        try {
            log.info("监听用户奖品发送消息 topic: {} message: {}", topic, message);
            BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendAwardMessageEvent.SendAwardMessage>>() {
            }.getType());
            SendAwardMessageEvent.SendAwardMessage sendAwardMessage = eventMessage.getData();

            // 发放奖品
            DistributeAwardEntity distributeAwardEntity = new DistributeAwardEntity();
            distributeAwardEntity.setUserId(sendAwardMessage.getUserId());
            distributeAwardEntity.setOrderId(sendAwardMessage.getOrderId());
            distributeAwardEntity.setAwardId(sendAwardMessage.getAwardId());
            distributeAwardEntity.setAwardConfig(sendAwardMessage.getAwardConfig());
            awardService.distributeAward(distributeAwardEntity);
        } catch (Exception e) {
            log.error("监听用户奖品发送消息，消费失败 topic: {} message: {}", topic, message);
            throw e;
        } finally {
            ack.acknowledge();
        }
    }

}
