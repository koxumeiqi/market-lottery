package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RuleTree;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author myz03
* @description 针对表【rule_tree(规则表-树)】的数据库操作Mapper
* @createDate 2024-03-12 20:46:33
* @Entity com.ly.infrastructure.persistent.po.RuleTree
*/
@Mapper
public interface RuleTreeDao extends BaseMapper<RuleTree> {

}




