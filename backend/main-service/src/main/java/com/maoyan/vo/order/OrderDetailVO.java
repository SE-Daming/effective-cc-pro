package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单详情
 *
 * @author maoyan
 */
@Data
@Schema(description = "订单详情")
public class OrderDetailVO {

    @Schema(description = "订单ID")
    private Long id;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "订单类型：1电影票 2卖品 3组合")
    private Integer type;

    @Schema(description = "订单状态：1待支付 2已支付 3已出票 4已完成 5已取消 6退款中 7已退款")
    private Integer status;

    @Schema(description = "订单总额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "实付金额")
    private BigDecimal payAmount;

    @Schema(description = "支付时间")
    private LocalDateTime payTime;

    @Schema(description = "支付方式")
    private String payType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "过期时间")
    private LocalDateTime expireTime;

    @Schema(description = "票务信息列表")
    private List<TicketDetail> tickets;

    @Schema(description = "卖品信息列表")
    private List<SnackDetail> snacks;

    @Schema(description = "优惠券信息")
    private CouponInfo coupon;

    /**
     * 票务详情
     */
    @Data
    @Schema(description = "票务详情")
    public static class TicketDetail {
        @Schema(description = "票务ID")
        private Long id;

        @Schema(description = "场次ID")
        private Long scheduleId;

        @Schema(description = "电影名称")
        private String movieTitle;

        @Schema(description = "电影海报")
        private String moviePoster;

        @Schema(description = "影院名称")
        private String cinemaName;

        @Schema(description = "影院地址")
        private String cinemaAddress;

        @Schema(description = "影厅名称")
        private String hallName;

        @Schema(description = "放映日期")
        private String showDate;

        @Schema(description = "放映时间")
        private String showTime;

        @Schema(description = "座位号")
        private String seatNo;

        @Schema(description = "票价")
        private BigDecimal ticketPrice;

        @Schema(description = "取票码")
        private String pickCode;

        @Schema(description = "取票二维码")
        private String pickQrcode;

        @Schema(description = "状态：1未取票 2已取票")
        private Integer status;
    }

    /**
     * 卖品详情
     */
    @Data
    @Schema(description = "卖品详情")
    public static class SnackDetail {
        @Schema(description = "卖品ID")
        private Long id;

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

    /**
     * 优惠券信息
     */
    @Data
    @Schema(description = "优惠券信息")
    public static class CouponInfo {
        @Schema(description = "优惠券ID")
        private Long id;

        @Schema(description = "优惠券名称")
        private String name;

        @Schema(description = "优惠值")
        private BigDecimal value;
    }

}
