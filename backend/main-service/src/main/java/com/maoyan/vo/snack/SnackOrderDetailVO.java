package com.maoyan.vo.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 卖品订单详情VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品订单详情")
public class SnackOrderDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单类型：2卖品")
    private Integer type;

    @Schema(description = "订单状态：1待支付 2已支付 3已完成 5已取消 7已退款")
    private Integer status;

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "影院地址")
    private String cinemaAddress;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "订单项列表")
    private List<OrderItemVO> items;

    @Schema(description = "优惠券信息")
    private CouponInfo coupon;

    @Data
    @Schema(description = "订单项")
    public static class OrderItemVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "订单项ID")
        private Long id;

        @Schema(description = "卖品ID")
        private Long snackId;

        @Schema(description = "卖品名称")
        private String snackName;

        @Schema(description = "规格")
        private String spec;

        @Schema(description = "数量")
        private Integer quantity;

        @Schema(description = "单价")
        private BigDecimal price;

        @Schema(description = "小计")
        private BigDecimal totalPrice;

        @Schema(description = "取货码")
        private String pickCode;

        @Schema(description = "状态：1未取货 2已取货")
        private Integer status;
    }

    @Data
    @Schema(description = "优惠券信息")
    public static class CouponInfo implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "优惠券ID")
        private Long id;

        @Schema(description = "优惠券名称")
        private String name;

        @Schema(description = "优惠金额")
        private BigDecimal value;
    }
}
