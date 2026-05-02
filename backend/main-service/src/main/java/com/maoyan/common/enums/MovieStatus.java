package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 电影状态枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum MovieStatus implements com.maoyan.common.BaseStatus {

    COMING_SOON(1, "即将上映"),
    SHOWING(2, "上映中"),
    OFFLINE(3, "下架");

    private final Integer code;
    private final String desc;

}
