package com.maoyan.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回状态码枚举
 *
 * @author maoyan
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    // 客户端错误 4xx
    FAIL(400, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),

    // 业务错误 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_DISABLED(1002, "用户已被禁用"),
    USER_PHONE_EXISTS(1003, "手机号已存在"),

    MOVIE_NOT_FOUND(1101, "电影不存在"),
    MOVIE_OFFLINE(1102, "电影已下架"),

    CINEMA_NOT_FOUND(1201, "影院不存在"),
    CINEMA_CLOSED(1202, "影院已关闭"),

    HALL_NOT_FOUND(1211, "影厅不存在"),

    SCHEDULE_NOT_FOUND(1301, "场次不存在"),
    SCHEDULE_CANCELLED(1302, "场次已取消"),
    SCHEDULE_NO_SEAT(1303, "该场次已无座位"),

    SEAT_LOCKED(1401, "座位已被锁定"),
    SEAT_SOLD(1402, "座位已被售出"),
    SEAT_NOT_AVAILABLE(1403, "座位不可用"),

    ORDER_NOT_FOUND(1501, "订单不存在"),
    ORDER_STATUS_ERROR(1502, "订单状态错误"),
    ORDER_EXPIRED(1503, "订单已过期"),
    ORDER_ALREADY_PAID(1504, "订单已支付"),
    ORDER_ALREADY_CANCELLED(1505, "订单已取消"),
    ORDER_CANNOT_CANCEL(1506, "订单无法取消"),
    ORDER_CANNOT_REFUND(1507, "订单无法退款"),

    COUPON_NOT_FOUND(1601, "优惠券不存在"),
    COUPON_EXPIRED(1602, "优惠券已过期"),
    COUPON_USED(1603, "优惠券已使用"),
    COUPON_NOT_AVAILABLE(1604, "优惠券不可用"),
    COUPON_LIMIT_EXCEEDED(1605, "优惠券领取已达上限"),

    SNACK_NOT_FOUND(1701, "卖品不存在"),
    SNACK_OFFLINE(1702, "卖品已下架"),
    SNACK_STOCK_NOT_ENOUGH(1703, "卖品库存不足"),

    FAVORITE_EXISTS(1801, "已收藏"),
    FAVORITE_NOT_FOUND(1802, "收藏不存在"),

    ADMIN_NOT_FOUND(1901, "管理员不存在"),
    ADMIN_PASSWORD_ERROR(1902, "密码错误"),
    ADMIN_DISABLED(1903, "管理员已被禁用"),

    // 服务器错误 5xx
    INTERNAL_ERROR(500, "服务器内部错误"),
    DATABASE_ERROR(501, "数据库错误"),
    NETWORK_ERROR(502, "网络错误");

    /**
     * 状态码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

}
