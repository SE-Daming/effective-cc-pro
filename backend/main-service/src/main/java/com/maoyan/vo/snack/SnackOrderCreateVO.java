package com.maoyan.vo.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 卖品订单创建结果VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品订单创建结果")
public class SnackOrderCreateVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "支付信息（微信支付参数）")
    private PayInfo payInfo;

    @Data
    @Schema(description = "支付信息")
    public static class PayInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "小程序AppId")
        private String appId;

        @Schema(description = "时间戳")
        private String timeStamp;

        @Schema(description = "随机字符串")
        private String nonceStr;

        @Schema(description = "预支付交易会话标识")
        private String packageValue;

        @Schema(description = "签名方式")
        private String signType;

        @Schema(description = "签名")
        private String paySign;
    }
}
