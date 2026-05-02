package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 退票检查响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "退票检查响应")
public class RefundCheckVO {

    @Schema(description = "是否可退票")
    private Boolean canRefund;

    @Schema(description = "退款金额（原实付金额）")
    private BigDecimal refundAmount;

    @Schema(description = "退票手续费")
    private BigDecimal refundFee;

    @Schema(description = "实际退款金额")
    private BigDecimal actualRefund;

    @Schema(description = "原因说明")
    private String reason;

}
