package com.maoyan.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 影院销售排行VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院销售排行")
public class CinemaRankingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "排名")
    private Integer rank;

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "影院地址")
    private String cinemaAddress;

    @Schema(description = "销售额")
    private BigDecimal salesAmount;

    @Schema(description = "订单数量")
    private Long orderCount;

    @Schema(description = "售票数量")
    private Long ticketCount;
}
