package com.ly.domain.strategy.service.rule.chain.impl;


import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.chain.AbstractLogicChain;
import com.ly.types.common.Constants;
import com.ly.types.common.LogicModelConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component(LogicModelConstants.LOGIC_BLACK_LIST)
@Slf4j
public class BackListLogicChain extends AbstractLogicChain {

    @Autowired
    private IStrategyRepository repository;

    @Override
    protected String ruleModel() {
        return LogicModelConstants.LOGIC_BLACK_LIST;
    }

    @Override
    public Integer logic(String userId, Long strategyId) {

        log.info("抽奖责任链-黑名单开始 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        // 查询规则值配置
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());
        String[] splitRuleValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.parseInt(splitRuleValue[0]);
        // 黑名单抽奖判断
        String[] userBlackIds = splitRuleValue[1].split(Constants.SPLIT);
        for (String userBlackId : userBlackIds) {
            if (userId.equals(userBlackId)) {
                log.info("抽奖责任链-黑名单接管 userId: {} strategyId: {} ruleModel: {} awardId: {}", userId, strategyId, ruleModel(), awardId);
                return awardId;
            }
        }
        // 过滤其他责任链
        log.info("抽奖责任链-黑名单放行 userId: {} strategyId: {} ruleModel: {}", userId, strategyId, ruleModel());
        return next().logic(userId, strategyId);
    }
}
