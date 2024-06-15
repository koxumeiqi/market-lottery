package com.ly.infrastructure.persistent.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 规则表-树节点连线
 * @TableName rule_tree_node_line
 */
@TableName(value ="rule_tree_node_line")
@Data
public class RuleTreeNodeLine implements Serializable {
    /**
     * 自增ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则Key节点 From
     */
    private String ruleNodeFrom;

    /**
     * 规则Key节点 To
     */
    private String ruleNodeTo;

    /**
     * 限定类型；1:=;2:>;3:<;4:>=;5<=;6:enum[枚举范围];
     */
    private String ruleLimitType;

    /**
     * 限定值（到下个节点）
     */
    private String ruleLimitValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        RuleTreeNodeLine other = (RuleTreeNodeLine) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTreeId() == null ? other.getTreeId() == null : this.getTreeId().equals(other.getTreeId()))
            && (this.getRuleNodeFrom() == null ? other.getRuleNodeFrom() == null : this.getRuleNodeFrom().equals(other.getRuleNodeFrom()))
            && (this.getRuleNodeTo() == null ? other.getRuleNodeTo() == null : this.getRuleNodeTo().equals(other.getRuleNodeTo()))
            && (this.getRuleLimitType() == null ? other.getRuleLimitType() == null : this.getRuleLimitType().equals(other.getRuleLimitType()))
            && (this.getRuleLimitValue() == null ? other.getRuleLimitValue() == null : this.getRuleLimitValue().equals(other.getRuleLimitValue()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTreeId() == null) ? 0 : getTreeId().hashCode());
        result = prime * result + ((getRuleNodeFrom() == null) ? 0 : getRuleNodeFrom().hashCode());
        result = prime * result + ((getRuleNodeTo() == null) ? 0 : getRuleNodeTo().hashCode());
        result = prime * result + ((getRuleLimitType() == null) ? 0 : getRuleLimitType().hashCode());
        result = prime * result + ((getRuleLimitValue() == null) ? 0 : getRuleLimitValue().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", treeId=").append(treeId);
        sb.append(", ruleNodeFrom=").append(ruleNodeFrom);
        sb.append(", ruleNodeTo=").append(ruleNodeTo);
        sb.append(", ruleLimitType=").append(ruleLimitType);
        sb.append(", ruleLimitValue=").append(ruleLimitValue);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}