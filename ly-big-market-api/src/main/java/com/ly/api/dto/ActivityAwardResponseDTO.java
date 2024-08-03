package com.ly.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAwardResponseDTO {

    private String userId;

    private Integer awardId;

    private String awardTitle;

    private String awardTime;

    /**
     * 抽奖状态描述
     */
    private String awardStateDesc;

}
