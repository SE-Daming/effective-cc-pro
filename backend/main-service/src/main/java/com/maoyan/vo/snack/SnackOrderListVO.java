package com.maoyan.vo.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 卖品订单列表项VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品订单列表项")
public class SnackOrderListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "卖品项数量")
    private Integer itemCount;
}
