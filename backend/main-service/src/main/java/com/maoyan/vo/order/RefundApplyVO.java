package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退票申请响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "退票申请响应")
public class RefundApplyVO {

    @Schema(description = "退款ID")
    private Long refundId;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退票手续费")
    private BigDecimal refundFee;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "提示消息")
    private String message;

}
