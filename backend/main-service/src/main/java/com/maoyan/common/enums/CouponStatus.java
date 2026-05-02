package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 优惠券状态枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum CouponStatus implements com.maoyan.common.BaseStatus {

    UNUSED(1, "未使用"),
    USED(2, "已使用"),
    EXPIRED(3, "已过期");

    private final Integer code;
    private final String desc;

    /**
     * 根据状态码获取枚举
     */
    public static CouponStatus getByCode(Integer code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

}
