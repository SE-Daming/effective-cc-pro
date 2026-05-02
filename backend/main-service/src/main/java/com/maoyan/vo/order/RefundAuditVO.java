package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退款审核响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "退款审核响应")
public class RefundAuditVO {

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "退票手续费")
    private BigDecimal refundFee;

}
