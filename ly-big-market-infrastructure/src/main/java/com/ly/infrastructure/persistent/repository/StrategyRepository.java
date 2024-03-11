package com.ly.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.ly.domain.strategy.model.entity.StrategyAwardEntity;
import com.ly.domain.strategy.model.entity.StrategyEntity;
import com.ly.domain.strategy.model.entity.StrategyRuleEntity;
import com.ly.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import com.ly.domain.strategy.repository.IStrategyRepository;
import com.ly.infrastructure.persistent.dao.IStrategyAwardDao;
import com.ly.infrastructure.persistent.dao.IStrategyDao;
import com.ly.infrastructure.persistent.dao.IStrategyRuleDao;
import com.ly.infrastructure.persistent.po.Strategy;
import com.ly.infrastructure.persistent.po.StrategyAward;
import com.ly.infrastructure.persistent.po.StrategyRule;
import com.ly.infrastructure.persistent.redis.RedissonService;
import com.ly.types.common.Constants;
import com.ly.types.exception.AppException;
import org.apache.catalina.startup.AddPortOffsetRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ly.types.enums.ResponseCode.UN_ASSEMBLED_STRATEGY_ARMORY;


@Repository
public class StrategyRepository implements IStrategyRepository {

    @Autowired
    private RedissonService redisService;

    @Autowired
    private IStrategyAwardDao strategyAwardDao;

    @Autowired
    private IStrategyDao strategyDao;

    @Autowired
    private IStrategyRuleDao strategyRuleDao;

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
        return StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
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
        long userLockCount = redisService.incr(cacheKey);
        return Long.valueOf(userLockCount);
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
        int userLockCount = redisService.getValue(cacheKey);
        return Long.valueOf(userLockCount);
    }
}
