package com.ly.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StrategyResponseDTO implements Serializable {

    private Long strategyId;

    private String strategyDesc;

}
