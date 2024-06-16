package com.ly.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ly.domain.activity.model.entity.SkuRechargeEntity;
import com.ly.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.ly.domain.rebate.event.SendRebateMessageEvent;
import com.ly.domain.rebate.model.vo.RebateTypeVO;
import com.ly.types.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 监听行为返利消息
 */
@Component
@Slf4j
public class RebateMessageCustomer {

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Value("${spring.kafka.topic.send_rebate}")
    private String topic;

    @KafkaListener(topics = {"${spring.kafka.topic.send_rebate}"})
    public void rebateMessage(String message, Acknowledgment ack) {
        try {
            log.info("监听用户行为返利消息 topic: {} message: {}", topic, message);
            // 1. 转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            // TODO 其他行为返利后续实现
            if (!RebateTypeVO.SKU.getCode().equals(rebateMessage.getRebateType())) {
                log.info("监听用户行为返利消息 - 非sku奖励暂时不处理 topic: {} message: {}", topic, message);
                ack.acknowledge();
                return;
            }
            // 2. 进行抽奖额度充值
            SkuRechargeEntity rechargeEntity = new SkuRechargeEntity();
            rechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig())); // sku类型的奖励返利配置即sku id
            rechargeEntity.setUserId(rebateMessage.getUserId());
            rechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
            raffleActivityAccountQuotaService.createOrder(rechargeEntity);
        } catch (Exception e) {
            log.error("监听用户行为返利消息，消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        } finally {
            ack.acknowledge();
        }
    }

}
