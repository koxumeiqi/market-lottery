package com.ly.domain.strategy.model.valobj;

import com.ly.domain.strategy.service.rule.factory.DefaultLogicFactory;
import com.ly.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 抽奖策略规则规则值对象；值对象，没有唯一ID，仅限于从数据库查询对象
 * @create 2024-01-13 09:30
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardRuleModelVO {

    private String ruleModels;

    public String[] raffleCenterRuleModelList() {
        String[] ruleModelValues = this.ruleModels.split(Constants.SPLIT);
        List<String> ans = new ArrayList<>();
        for (String ruleModelValue : ruleModelValues) {
            if (DefaultLogicFactory.LogicModel.isCenter(ruleModelValue)) {
                ans.add(ruleModelValue);
            }
        }
        return ans.toArray(new String[0]);
    }

}
