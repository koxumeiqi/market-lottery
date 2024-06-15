package com.ly.infrastructure.persistent.dao;

import com.ly.infrastructure.persistent.po.RuleTreeNodeLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author myz03
* @description 针对表【rule_tree_node_line(规则表-树节点连线)】的数据库操作Mapper
* @createDate 2024-03-12 20:46:33
* @Entity com.ly.infrastructure.persistent.po.RuleTreeNodeLine
*/
@Mapper
public interface RuleTreeNodeLineDao extends BaseMapper<RuleTreeNodeLine> {

}




