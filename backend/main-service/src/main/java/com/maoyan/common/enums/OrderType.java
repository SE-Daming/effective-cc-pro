package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单类型枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum OrderType implements com.maoyan.common.BaseStatus {

    TICKET(1, "电影票"),
    SNACK(2, "卖品"),
    COMBO(3, "组合");

    private final Integer code;
    private final String desc;

}
