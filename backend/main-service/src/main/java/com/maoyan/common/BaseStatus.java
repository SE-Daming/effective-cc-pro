package com.maoyan.common;

/**
 * 基础状态接口
 *
 * @author maoyan
 */
public interface BaseStatus {

    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取描述
     */
    String getDesc();

}
