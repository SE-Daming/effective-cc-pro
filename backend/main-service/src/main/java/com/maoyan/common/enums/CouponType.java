package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 优惠券类型枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum CouponType implements com.maoyan.common.BaseStatus {

    FULL_REDUCTION(1, "满减券"),
    DISCOUNT(2, "折扣券"),
    INSTANT(3, "立减券"),
    EXCHANGE(4, "兑换券");

    private final Integer code;
    private final String desc;

}
