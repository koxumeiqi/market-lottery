package com.ly.domain.strategy.service.rule.chain.impl;


import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.ly.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.ly.types.common.Constants;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component(LogicModelConstants.LOGIC_BLACK_LIST)
@Slf4j
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BackListLogicChain extends AbstractLogicChain {

    @Autowired
    private IStrategyRepository repository;

    @Override
    protected String ruleModel() {
        return LogicModelConstants.LOGIC_BLACK_LIST;
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO logic(String userId, Long strategyId, String orderId) {

        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        if (ruleValue == null) return next().logic(userId, strategyId, orderId);
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);
        // 黑名单抽奖判断
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(ruleModel())
                        // 写入默认配置黑名单奖品值 0.01 ~ 1 积分，也可以配置到数据库表中
                        .awardRuleValue("0.01,1")
                        .build();
            }
        }
        // 过滤其他责任链
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId, orderId);
    }
}
