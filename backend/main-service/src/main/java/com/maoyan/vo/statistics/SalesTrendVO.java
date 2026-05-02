package com.maoyan.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售趋势项VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "销售趋势项")
public class SalesTrendVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "日期/月份")
    private String date;

    @Schema(description = "订单数")
    private Long orderCount;

    @Schema(description = "销售额")
    private BigDecimal amount;

    @Schema(description = "票数")
    private Long ticketCount;
}
