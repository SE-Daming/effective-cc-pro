package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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

    /**
     * 根据状态码获取枚举
     */
    public static CouponType getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
