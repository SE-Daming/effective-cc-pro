package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

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

}
