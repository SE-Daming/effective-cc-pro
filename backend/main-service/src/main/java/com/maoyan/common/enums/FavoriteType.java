package com.maoyan.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 收藏类型枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum FavoriteType implements com.maoyan.common.BaseStatus {

    MOVIE(1, "电影"),
    CINEMA(2, "影院");

    private final Integer code;
    private final String desc;

}
