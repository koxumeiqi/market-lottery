package com.ly.domain.strategy.service.rule.tree.impl;

import com.ly.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.tree.ILogicTreeNode;
import com.ly.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author liyang
 * @description 次数锁节点(进行了几次抽奖才能正常抽奖)
 * @create 2024-01-27 11:22
 */
@Service("rule_lock")
@Slf4j
public class RuleLockLogicTreeNode implements ILogicTreeNode {

    @Autowired
    private IStrategyRepository repository;

    private Integer userCount = 1000000; // TODO 用户抽奖次数

    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId,
                                                     Long strategyId,
                                                     Integer awardId,
                                                     String ruleValue
            , Date endDateTime) {
        log.info("规则过滤-次数锁 userId:{} strategyId:{} awardId:{}", userId, strategyId, awardId);
        // 获取到这个奖品对应锁定的次数
        long raffleCount = 0L;
        try {
            raffleCount = Long.parseLong(ruleValue);
        } catch (Exception e) {
            throw new RuntimeException("规则过滤-次数锁异常 ruleValue: " + ruleValue + " 配置不正确");
        }
        // 判断是否存在，和是否大于限定值，是的话放行
        // 查询用户抽奖次数 - 当天的；策略ID:活动ID 1:1 的配置，可以直接用 strategyId 查询。
        Integer userRaffleCount = repository.queryTodayUserRaffleCount(userId, strategyId);
//        Long userCount = repository.rQueryLockCount(userId, awardId, strategyId, lockCount);
        if (userRaffleCount > raffleCount) {
            log.info("规则过滤-次数锁【放行】 userId:{} strategyId:{} awardId:{} raffleCount:{} userRaffleCount:{}", userId, strategyId, awardId, userRaffleCount, userRaffleCount);
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckType(RuleLogicCheckTypeVO.ALLOW)
                    .build();
        }

        log.info("规则过滤-次数锁【拦截】 userId:{} strategyId:{} awardId:{} raffleCount:{} userRaffleCount:{}", userId, strategyId, awardId, userRaffleCount, userRaffleCount);

        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckType(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
