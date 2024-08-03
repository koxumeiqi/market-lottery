package com.ly.domain.award.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 奖品状态枚举值对象 【值对象，用于描述对象属性的值，一个对象中，一个属性，有多个状态值。】
 * @create 2024-04-06 09:13
 */
@Getter
@AllArgsConstructor
public enum AwardStateVO {

    create("create", "创建"),
    complete("completed", "发奖完成"),
    fail("fail", "发奖失败"),
    ;

    private final String code;
    private final String desc;

    public static AwardStateVO getAwardStateVO(String awardState) {
        for (AwardStateVO awardStateVO : AwardStateVO.values()) {
            if (awardStateVO.getCode().equals(awardState)) {
                return awardStateVO;
            }
        }
        throw new IllegalArgumentException("奖品状态异常");
    }

    public static String getAwardStateDesc(AwardStateVO awardStatus) {
        switch (awardStatus) {
            case create:
                return "待发货";
            case complete:
                return "发奖完成";
            case fail:
                return "发奖失败";
            default:
                return "";
        }
    }
}
