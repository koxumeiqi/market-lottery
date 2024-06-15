package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RuleTreeNode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author myz03
* @description 针对表【rule_tree_node(规则表-树节点)】的数据库操作Mapper
* @createDate 2024-03-12 20:46:33
* @Entity com.ly.infrastructure.persistent.po.RuleTreeNode
*/
@Mapper
public interface RuleTreeNodeDao extends BaseMapper<RuleTreeNode> {

    List<RuleTreeNode> queryRuleLocks(String[] treeIds);

}




