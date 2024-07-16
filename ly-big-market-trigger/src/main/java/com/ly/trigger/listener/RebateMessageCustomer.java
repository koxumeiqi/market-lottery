package com.ly.trigger.listener;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.ly.domain.activity.model.entity.SkuRechargeEntity;
import com.ly.domain.activity.service.IRaffleActivityAccountQuotaService;
import com.ly.domain.credit.model.entity.TradeEntity;
import com.ly.domain.credit.model.valobj.TradeNameVO;
import com.ly.domain.credit.model.valobj.TradeTypeVO;
import com.ly.domain.credit.service.ICreditAdjustService;
import com.ly.domain.rebate.event.SendRebateMessageEvent;
import com.ly.domain.rebate.model.vo.RebateTypeVO;
import com.ly.types.enums.ResponseCode;
import com.ly.types.event.BaseEvent;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 监听行为返利消息
 */
@Component
@Slf4j
public class RebateMessageCustomer {

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String topic;

    @Resource
    private ICreditAdjustService creditAdjustService;

    @RabbitListener(queuesToDeclare = @Queue(value = "${spring.rabbitmq.topic.send_rebate}"))
    public void rebateMessage(String message) {
        try {
            log.info("监听用户行为返利消息 topic: {} message: {}", topic, message);
            // 1. 转换消息
            BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage> eventMessage = JSON.parseObject(message, new TypeReference<BaseEvent.EventMessage<SendRebateMessageEvent.RebateMessage>>() {
            }.getType());
            SendRebateMessageEvent.RebateMessage rebateMessage = eventMessage.getData();
            // 入账奖励
            switch (rebateMessage.getRebateType()) {
                case "sku":
                    SkuRechargeEntity rechargeEntity = new SkuRechargeEntity();
                    rechargeEntity.setSku(Long.valueOf(rebateMessage.getRebateConfig())); // sku类型的奖励返利配置即sku id
                    rechargeEntity.setUserId(rebateMessage.getUserId());
                    rechargeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    raffleActivityAccountQuotaService.createOrder(rechargeEntity);
                    break;
                case "integral":
                    TradeEntity tradeEntity = new TradeEntity();
                    tradeEntity.setUserId(rebateMessage.getUserId());
                    tradeEntity.setTradeName(TradeNameVO.REBATE);
                    tradeEntity.setTradeType(TradeTypeVO.FORWARD);
                    tradeEntity.setOutBusinessNo(rebateMessage.getBizId());
                    tradeEntity.setAmount(new BigDecimal(rebateMessage.getRebateConfig()));
                    creditAdjustService.createOrder(tradeEntity);
                    break;
            }
        } catch (AppException e) {
            if (ResponseCode.INDEX_DUP.getCode().equals(e.getCode())) {
                log.warn("监听用户行为返利消息，消费重复 topic: {} message: {}", topic, message, e);
                return;
            }
            throw e;
        } catch (Exception e) {
            log.error("监听用户行为返利消息，消费失败 topic: {} message: {}", topic, message, e);
            throw e;
        } finally {
//            ack.acknowledge();
        }
    }

}
