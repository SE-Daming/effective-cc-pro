package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 领取优惠券结果VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "领取优惠券结果")
public class ReceiveCouponVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户优惠券ID")
    private Long userCouponId;

    @Schema(description = "优惠券名称")
    private String name;

    @Schema(description = "有效期开始")
    private LocalDateTime validStart;

    @Schema(description = "有效期结束")
    private LocalDateTime validEnd;

}
