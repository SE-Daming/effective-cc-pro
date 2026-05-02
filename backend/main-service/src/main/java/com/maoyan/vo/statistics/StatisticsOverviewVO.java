package com.maoyan.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售统计概览VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "销售统计概览")
public class StatisticsOverviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单总数")
    private Long totalOrders;

    @Schema(description = "销售总额")
    private BigDecimal totalAmount;

    @Schema(description = "售票总数")
    private Long totalTickets;

    @Schema(description = "购票用户数")
    private Long totalUsers;

    @Schema(description = "新增用户数")
    private Long newUsers;

    @Schema(description = "退款订单数")
    private Long refundCount;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;
}
