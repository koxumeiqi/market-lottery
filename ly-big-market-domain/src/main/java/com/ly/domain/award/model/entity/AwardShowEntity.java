package com.ly.domain.award.model.entity;


import com.ly.domain.award.model.vo.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardShowEntity {

    private String userId;

    private Integer awardId;

    private String awardTitle;

    private String awardTime;

    /**
     * 抽奖状态描述
     */
    private AwardStateVO awardStatus;

}
