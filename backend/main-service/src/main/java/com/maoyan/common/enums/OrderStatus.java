package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum OrderStatus implements com.maoyan.common.BaseStatus {

    UNPAID(1, "待支付"),
    PAID(2, "已支付"),
    TICKETED(3, "已出票"),
    COMPLETED(4, "已完成"),
    CANCELLED(5, "已取消"),
    REFUNDING(6, "退款中"),
    REFUNDED(7, "已退款");

    private final Integer code;
    private final String desc;

}
