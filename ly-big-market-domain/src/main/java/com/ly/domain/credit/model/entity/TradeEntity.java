package com.ly.domain.credit.model.entity;

import com.ly.domain.credit.model.valobj.TradeNameVO;
import com.ly.domain.credit.model.valobj.TradeTypeVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 添加积分额度实体；交易对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeEntity {

    /**
     * 用户 ID
     */
    private String userId;

    /**
     * 交易名称
     */
    private TradeNameVO tradeName;

    /**
     * 交易类型：交易类型：forward - 正向、reverse - 逆向
     */
    private TradeTypeVO tradeType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 业务防重ID - 外部透传。返利、行为等唯一标识
     */
    private String outBusinessNo;

}
