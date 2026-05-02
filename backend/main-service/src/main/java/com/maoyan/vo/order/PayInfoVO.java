package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 支付信息响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "支付信息响应")
public class PayInfoVO {

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "支付信息（微信支付所需参数）")
    private Map<String, Object> payInfo;

}
