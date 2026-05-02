package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 最佳优惠券VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "最佳优惠券")
public class BestCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户优惠券ID")
    private Long id;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "可优惠金额")
    private BigDecimal discountAmount;

}
