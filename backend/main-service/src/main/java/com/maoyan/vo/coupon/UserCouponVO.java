package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "用户优惠券")
public class UserCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户优惠券ID")
    private Long id;

    @Schema(description = "优惠券ID")
    private Long couponId;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "类型：1满减 2折扣 3立减 4兑换")
    private Integer type;

    @Schema(description = "类型名称")
    private String typeName;

    @Schema(description = "优惠值")
    private BigDecimal value;

    @Schema(description = "最低消费金额")
    private BigDecimal minAmount;

    @Schema(description = "有效期开始")
    private LocalDateTime validStart;

    @Schema(description = "有效期结束")
    private LocalDateTime validEnd;

    @Schema(description = "状态：1未使用 2已使用 3已过期")
    private Integer status;

    @Schema(description = "状态名称")
    private String statusName;

    @Schema(description = "适用范围：1全部 2指定电影 3指定影院")
    private Integer scope;

    @Schema(description = "适用范围名称")
    private String scopeName;

    @Schema(description = "使用时间")
    private LocalDateTime useTime;

}
