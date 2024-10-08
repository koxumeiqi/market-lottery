package com.ly.domain.strategy.service.raffle;

import com.ly.domain.strategy.model.entity.StrategyAwardEntity;
import com.ly.domain.strategy.model.valobj.RuleTreeVO;
import com.ly.domain.strategy.model.valobj.RuleWeightVO;
import com.ly.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.ly.domain.strategy.model.valobj.StrategyAwardStockKeyVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.armory.IStrategyDispatch;
import com.ly.domain.strategy.service.rule.chain.ILogicChain;
import com.ly.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.ly.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import com.ly.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DefaultRaffleStrategy extends AbstractRaffleStrategy implements IRaffleStock, IRaffleAward, IRaffleRule {


    public DefaultRaffleStrategy(IStrategyRepository repository, IStrategyDispatch strategyDispatch,
                                 DefaultTreeFactory defaultTreeFactory, DefaultChainFactory defaultChainFactory) {
        super(repository, strategyDispatch, defaultTreeFactory, defaultChainFactory);
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId, String orderId) {
        ILogicChain logicChain = defaultChainFactory.openLogicChain(strategyId);
        return logicChain.logic(userId, strategyId, orderId);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData raffleLogicTree(String userId, Long strategyId, Integer awardId) {
        return this.raffleLogicTree(userId, strategyId, awardId, null);
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData raffleLogicTree(String userId, Long strategyId, Integer awardId, Date endDateTime) {
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId, awardId);
        if (strategyAwardRuleModelVO == null) {
            return DefaultTreeFactory.StrategyAwardData.builder().awardId(awardId).build();
        }
        RuleTreeVO ruleTreeVO = repository.queryRuleTreeVOByTreeId(strategyAwardRuleModelVO.getRuleModels());
        if (null == ruleTreeVO) {
            throw new RuntimeException("存在抽奖策略配置的规则模型 Key，未在库表 rule_tree、rule_tree_node、rule_tree_line 配置对应的规则树信息 " + strategyAwardRuleModelVO.getRuleModels());
        }
        IDecisionTreeEngine treeEngine = defaultTreeFactory.openLogicTree(ruleTreeVO);
        return treeEngine.process(userId, strategyId, awardId, endDateTime);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException {
        return repository.takeQueueValue();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        repository.updateStrategyAwardStock(strategyId, awardId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId) {
        return repository.queryStrategyAwardList(strategyId);
    }

    @Override
    public List<StrategyAwardEntity> queryRaffleStrategyAwardListByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return this.queryRaffleStrategyAwardList(strategyId);
    }

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        return repository.queryAwardRuleLockCount(treeIds);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        return repository.queryAwardRuleWeight(strategyId);
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeightByActivityId(Long activityId) {
        Long strategyId = repository.queryStrategyIdByActivityId(activityId);
        return queryAwardRuleWeight(strategyId);
    }
}
