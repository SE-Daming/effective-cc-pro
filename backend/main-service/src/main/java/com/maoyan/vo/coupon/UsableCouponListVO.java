package com.maoyan.vo.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 可用优惠券列表VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "可用优惠券列表")
public class UsableCouponListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "可用优惠券列表")
    private List<UsableCouponVO> list;

    @Schema(description = "最佳优惠券")
    private BestCouponVO bestCoupon;

}
