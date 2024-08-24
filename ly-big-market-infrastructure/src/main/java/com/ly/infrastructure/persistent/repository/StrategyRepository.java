package com.ly.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.google.protobuf.ServiceException;
import com.ly.domain.strategy.model.entity.StrategyAwardEntity;
import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.model.entity.StrategyRuleEntity;
import com.ly.domain.strategy.model.valobj.*;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import com.ly.infrastructure.persistent.dao.*;
import com.ly.infrastructure.persistent.po.*;
import com.ly.infrastructure.persistent.redis.RedissonService;
import com.ly.types.common.Constants;
import com.ly.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.startup.AddPortOffsetRule;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static com.ly.types.enums.ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY;


@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {

    @Autowired
    private RedissonService redisService;

    @Autowired
    private IStrategyAwardDao strategyAwardDao;

    @Autowired
    private IStrategyDao strategyDao;

    @Autowired
    private IStrategyRuleDao strategyRuleDao;

    @Autowired
    private RuleTreeDao ruleTreeDao;

    @Autowired
    private RuleTreeNodeDao ruleTreeNodeDao;

    @Autowired
    private RuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Autowired
    private RaffleActivityMapper raffleActivityMapper;

    @Autowired
    private UserRaffleOrderDao userRaffleOrderDao;

    @Autowired
    private RaffleActivityAccountDayDao raffleActivityAccountDayDao;

    @Autowired
    private RDelayedQueue delayedQueue;

    @Autowired
    private RBlockingQueue blockingQueue;

    @Resource
    private RaffleActivityAccountMapper raffleActivityAccountMapper;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        // 优先从缓存中获取
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;
        // 从库中获取数据
        List<StrategyAward> strategyAwards = strategyAwardDao.selectList(new LambdaQueryWrapper<StrategyAward>()
                .eq(StrategyAward::getStrategyId, strategyId));
        strategyAwardEntities = new ArrayList<>(strategyAwards.size());
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyAwardEntity strategyAwardEntity = StrategyAwardEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubtitle(strategyAward.getAwardSubtitle())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .sort(strategyAward.getSort())
                    .ruleModels(strategyAward.getRuleModels())
                    .build();
            strategyAwardEntities.add(strategyAwardEntity);
        }
        redisService.setValue(cacheKey, strategyAwardEntities);
        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange,
                                                  Map<Integer, Integer> shuffleStrategyAwardSearchRateTable) {
        // 先存储表格大小
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key, rateRange);
        // 后存储表格
        Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        cacheRateTable.putAll(shuffleStrategyAwardSearchRateTable);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return getRateRange(String.valueOf(strategyId));
    }

    @Override
    public int getRateRange(String key) {
        String cacheKey = Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key;
        if (!redisService.isExists(cacheKey)) {
            throw new AppException(UN_ASSEMBLED_STRATEGY_ARMORY.getCode(), cacheKey + Constants.COLON + UN_ASSEMBLED_STRATEGY_ARMORY.getInfo());
        }
        return redisService.getValue(cacheKey);
    }

    @Override
    public Integer getStrategyAwardAssemble(String key, int i) {
        /*Map<Integer, Integer> cacheRateTable = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        Integer awardId = cacheRateTable.get(i);
        return awardId;*/
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key, i);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        // 优先从缓存中获取
        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (null != strategyEntity) return strategyEntity;
        Strategy strategy = strategyDao.selectOne(new LambdaQueryWrapper<Strategy>()
                .eq(Strategy::getStrategyId, strategyId));
        if (null == strategy) return StrategyEntity.builder().build();
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;
    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight) {
        StrategyRule strategyRule = strategyRuleDao.selectOne(new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
                .eq(StrategyRule::getRuleModel, ruleWeight));
        if (strategyRule == null) return null;
        return StrategyRuleEntity.builder()
                .strategyId(strategyRule.getStrategyId())
                .awardId(strategyRule.getAwardId())
                .ruleType(strategyRule.getRuleType())
                .ruleModel(strategyRule.getRuleModel())
                .ruleValue(strategyRule.getRuleValue())
                .ruleDesc(strategyRule.getRuleDesc())
                .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        StrategyRule strategyRule = strategyRuleDao.selectOne(new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
//                .eq(StrategyRule::getAwardId, awardId)
                .eq(StrategyRule::getRuleModel, ruleModel));
        return strategyRule.getRuleValue();
    }

    @Override
    public String queryStrategyWhiteRuleValue(Long strategyId, String ruleModel) {
        StrategyRule strategyRule = strategyRuleDao.selectOne(new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
                .eq(StrategyRule::getRuleModel, ruleModel));
        return strategyRule.getRuleValue();
    }

    @Override
    public String getLockCount(Integer awardId, Long strategyId, String ruleModel) {
        StrategyRule strategyRule = strategyRuleDao.selectOne(new LambdaQueryWrapper<StrategyRule>()
                .eq(StrategyRule::getStrategyId, strategyId)
                .eq(StrategyRule::getAwardId, awardId)
                .eq(StrategyRule::getRuleModel, ruleModel)
        );
        return strategyRule.getRuleValue();
    }

    @Override
    public Long updateLockCount(String userId, Integer awardId, Long strategyId) {
        // key是strategyId+userId，value是lockCount
        String cacheKey = Constants.RedisKey.STRATEGY_USER_AWARD_LOCK_KEY + strategyId + Constants.COLON + userId;
        long incr = redisService.incr(cacheKey);
        return incr;
//        return Long.valueOf(userLockCount);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = strategyAwardDao.selectOne(
                new LambdaQueryWrapper<StrategyAward>()
                        .eq(StrategyAward::getStrategyId, strategyId)
                        .eq(StrategyAward::getAwardId, awardId)
        );
        return StrategyAwardRuleModelVO.builder()
                .ruleModels(strategyAward.getRuleModels())
                .build();
    }

    @Override
    public Long rQueryLockCount(String userId, Integer awardId, Long strategyId, Long lockCount) {
        // key是strategyId+userId，value是lockCount
        String cacheKey = Constants.RedisKey.STRATEGY_USER_AWARD_LOCK_KEY + strategyId + Constants.COLON + userId;
        return redisService.incr(cacheKey) - 1;
    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {

        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        RuleTreeVO ruleTreeVOCache = redisService.getValue(cacheKey);
        if (null != ruleTreeVOCache) return ruleTreeVOCache;
        RuleTree ruleTree = ruleTreeDao.selectOne(new LambdaQueryWrapper<RuleTree>().eq(RuleTree::getTreeId, treeId));
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.selectList(new LambdaQueryWrapper<RuleTreeNode>().eq(RuleTreeNode::getTreeId, treeId));
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.selectList(new LambdaQueryWrapper<RuleTreeNodeLine>().eq(RuleTreeNodeLine::getTreeId, treeId));

        // 1. tree node line
        Map<String, List<RuleTreeNodeLineVO>> ruleTreeMapLineMap = new HashMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(ruleTreeNodeLine.getTreeId())
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOList = ruleTreeMapLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOList.add(ruleTreeNodeLineVO);
        }
        // 2. tree node 转换为Map结构
        Map<String, RuleTreeNodeVO> treeNodeMap = new HashMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(ruleTreeNode.getTreeId())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeMapLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            treeNodeMap.put(ruleTreeNode.getRuleKey(), ruleTreeNodeVO);
        }

        // 3. 构建 Rule Tree
        RuleTreeVO ruleTreeVODB = RuleTreeVO.builder()
                .treeId(ruleTree.getTreeId())
                .treeName(ruleTree.getTreeName())
                .treeDesc(ruleTree.getTreeDesc())
                .treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
                .treeNodeMap(treeNodeMap)
                .build();

        redisService.setValue(cacheKey, ruleTreeVODB);
        return ruleTreeVODB;
    }

    @Override
    public void cacheStrategyAwardCount(String cacheKey, Integer awardCount) {
        if (redisService.isExists(cacheKey)) return;
        redisService.setAtomicLong(cacheKey, awardCount);
    }

    @Override
    public Boolean subtractionAwardStock(String cacheKey, Date endDateTime) {
        long nowStock = redisService.decr(cacheKey);
        if (nowStock < 0) {
            // 库存小于0，恢复为0个
            redisService.setAtomicLong(cacheKey, 0);
            return false;
        }
        // 防止手动处理缓存库存引起超卖，可用的库存key都被加锁了
        String lockKey = cacheKey + Constants.UNDERLINE + nowStock;
        Boolean lock = false;
        if (endDateTime != null) {
            long expireMillis = endDateTime.getTime() - System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1);
            lock = redisService.setNx(lockKey, expireMillis, TimeUnit.MILLISECONDS);
        } else {
            lock = redisService.setNx(lockKey);
        }
        if (!lock) {
            log.info("策略奖品库存加锁失败 {}", lockKey);
        }
        return lock;
    }

    @Override
    public void awardStockConsumeSendQueue(StrategyAwardStockKeyVO strategyAwardStockKeyVO) {
//        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_COUNT_QUERY_KEY;
//        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue(cacheKey);
//        RDelayedQueue<Object> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        delayedQueue.offer(strategyAwardStockKeyVO, 3, TimeUnit.SECONDS);
    }

    @Override
    public StrategyAwardStockKeyVO takeQueueValue() {
        return (StrategyAwardStockKeyVO) blockingQueue.poll();
    }

    @Override
    public void updateStrategyAwardStock(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
        strategyAwardDao.updateStrategyAwardStock(strategyAward);
    }

    @Override
    public StrategyAwardEntity queryStrategyAwardEntity(Long strategyId, Integer awardId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId + Constants.UNDERLINE + awardId;
        StrategyAwardEntity strategyAwardEntity = redisService.getValue(cacheKey);
        if (null != strategyAwardEntity) return strategyAwardEntity;
        StrategyAward strategyAward = strategyAwardDao.selectOne(new LambdaQueryWrapper<StrategyAward>()
                .eq(StrategyAward::getStrategyId, strategyId)
                .eq(StrategyAward::getAwardId, awardId));
        strategyAwardEntity = StrategyAwardEntity.builder()
                .strategyId(strategyAward.getStrategyId())
                .awardId(strategyAward.getAwardId())
                .awardTitle(strategyAward.getAwardTitle())
                .awardSubtitle(strategyAward.getAwardSubtitle())
                .awardCount(strategyAward.getAwardCount())
                .awardCountSurplus(strategyAward.getAwardCountSurplus())
                .awardRate(strategyAward.getAwardRate())
                .sort(strategyAward.getSort())
                .build();
        // 缓存结果
        redisService.setValue(cacheKey, strategyAwardEntity);
        // 返回数据
        return strategyAwardEntity;
    }

    @Override
    public List<StrategyEntity> fetchStrategyList() {
        List<Strategy> strategies = strategyDao.selectList(null);
        List<StrategyEntity> strategyEntityList = new ArrayList<>();
        strategies.forEach(strategy -> {
            StrategyEntity strategyEntity = StrategyEntity.builder()
                    .strategyId(strategy.getStrategyId())
                    .strategyDesc(strategy.getStrategyDesc())
                    .ruleModels(strategy.getRuleModels())
                    .build();
            strategyEntityList.add(strategyEntity);
        });
        return strategyEntityList;
    }

    @Override
    public Long queryStrategyIdByActivityId(Long activityId) {
        return raffleActivityMapper.queryStrategyIdByActivityId(activityId);
    }

    @Override
    public Integer queryTodayUserRaffleCount(String userId, Long strategyId) {
        // 活动ID
        Long activityId = raffleActivityMapper.queryActivityIdByStrategyId(strategyId);
        // 封装参数
        RaffleActivityAccountDay raffleActivityAccountDayReq = new RaffleActivityAccountDay();
        raffleActivityAccountDayReq.setUserId(userId);
        raffleActivityAccountDayReq.setActivityId(activityId);
        raffleActivityAccountDayReq.setDay(raffleActivityAccountDayReq.currentDay());
        RaffleActivityAccountDay raffleActivityAccountDay = raffleActivityAccountDayDao.queryActivityAccountDayByUserId(raffleActivityAccountDayReq);
        if (null == raffleActivityAccountDay) return 0;
        // 总次数 - 剩余的，等于今日参与的
        return raffleActivityAccountDay.getDayCount() - raffleActivityAccountDay.getDayCountSurplus();
    }

    @Override
    public Map<String, Integer> queryAwardRuleLockCount(String[] treeIds) {
        if (null == treeIds || treeIds.length == 0) return new HashMap<>();
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleLocks(Arrays.asList(treeIds));
        Map<String, Integer> ans = new HashMap<>();
        for (RuleTreeNode node : ruleTreeNodes) {
            String treeId = node.getTreeId();
            String ruleValue = node.getRuleValue();
            ans.put(treeId, Integer.valueOf(ruleValue));
        }
        return ans;
    }

    @Override
    public Integer queryActivityAccountTotalUseCount(String userId, Long activityId) {
        RaffleActivityAccount raffleActivityAccount = raffleActivityAccountMapper.queryActivityAccountByUserId(RaffleActivityAccount.builder()
                .userId(userId)
                .activityId(activityId)
                .build());
        // 返回计算使用量
        return raffleActivityAccount.getTotalCount() - raffleActivityAccount.getTotalCountSurplus();
    }

    @Override
    public List<RuleWeightVO> queryAwardRuleWeight(Long strategyId) {
        // 优先从缓存获取
        String cacheKey = Constants.RedisKey.STRATEGY_RULE_WEIGHT_KEY + strategyId;
        List<RuleWeightVO> ruleWeightVOS = redisService.getValue(cacheKey);
        if (null != ruleWeightVOS) return ruleWeightVOS;

        ruleWeightVOS = new ArrayList<>();
        // 1. 查询权重规则配置
        StrategyRule strategyRuleReq = new StrategyRule();
        strategyRuleReq.setStrategyId(strategyId);
        strategyRuleReq.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        String ruleValue = strategyRuleDao.queryStrategyRuleValue(strategyRuleReq);
        // 2. 借助实体对象转换规则
        StrategyRuleEntity strategyRuleEntity = new StrategyRuleEntity();
        strategyRuleEntity.setRuleModel(DefaultChainFactory.LogicModel.RULE_WEIGHT.getCode());
        strategyRuleEntity.setRuleValue(ruleValue);
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        // 3. 遍历规则组装奖品配置
        Set<String> ruleWeightKeys = ruleWeightValues.keySet();
        for (String ruleWeightKey : ruleWeightKeys) {
            List<Integer> awardIds = ruleWeightValues.get(ruleWeightKey);
            List<RuleWeightVO.Award> awardList = new ArrayList<>();
            // 也可以修改为一次从数据库查询
            for (Integer awardId : awardIds) {
                StrategyAward strategyAwardReq = new StrategyAward();
                strategyAwardReq.setStrategyId(strategyId);
                strategyAwardReq.setAwardId(awardId);
                StrategyAward strategyAward = strategyAwardDao.queryStrategyAward(strategyAwardReq);
                awardList.add(RuleWeightVO.Award.builder()
                        .awardId(awardId)
                        .awardTitle(strategyAward.getAwardTitle())
                        .build());
            }

            ruleWeightVOS.add(RuleWeightVO.builder()
                    .ruleValue(ruleValue)
                    .weight(Integer.valueOf(ruleWeightKey.split(Constants.COLON)[0]))
                    .awardIds(awardIds)
                    .awardList(awardList)
                    .build());
        }

        // 设置缓存 - 实际场景中，这类数据，可以在活动下架的时候统一清空缓存。
        redisService.setValue(cacheKey, ruleWeightVOS, 60 * 60 * 1000);

        return ruleWeightVOS;
    }

    @Override
    public Long queryActivityIdByOrderId(String userId, String orderId) {
        UserRaffleOrder userRaffleOrderReq = new UserRaffleOrder();
        userRaffleOrderReq.setUserId(userId);
        userRaffleOrderReq.setOrderId(orderId);
        UserRaffleOrder userRaffleOrder = userRaffleOrderDao.queryByOrderId(userRaffleOrderReq);
        if (Objects.isNull(userRaffleOrder)) {
            log.error("用户:{} 参与活动抽奖的订单有误 orderId:{}", userId, orderId);
            throw new RuntimeException("用户参与活动抽奖有误");
        }
        return userRaffleOrder.getActivityId();
    }

}
