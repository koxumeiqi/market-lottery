package com.ly.domain.strategy.model.entity;


import lombok.Data;

@Data
public class AlreadyGetAward {

    /**
     * 是否已经得到奖
     */
    private boolean flag;

    private Integer awardId;

}
