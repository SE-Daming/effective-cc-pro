package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 座位锁定状态枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum SeatLockStatus implements com.maoyan.common.BaseStatus {

    LOCKED(1, "锁定"),
    SOLD(2, "已售"),
    RELEASED(3, "释放");

    private final Integer code;
    private final String desc;

}
