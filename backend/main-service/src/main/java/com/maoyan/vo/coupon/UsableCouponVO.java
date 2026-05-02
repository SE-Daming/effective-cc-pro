package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 可用优惠券VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "可用优惠券")
public class UsableCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户优惠券ID")
    private Long id;

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

    @Schema(description = "可优惠金额")
    private BigDecimal discountAmount;

    @Schema(description = "有效期结束")
    private LocalDateTime validEnd;

}
