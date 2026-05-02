package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户优惠券实体
 *
 * @author maoyan
 */
@Data
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 优惠券ID
     */
    private Long couponId;

    /**
     * 优惠券名称（冗余）
     */
    private String couponName;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 优惠值
     */
    private BigDecimal value;

    /**
     * 最低消费
     */
    private BigDecimal minAmount;

    /**
     * 有效期开始
     */
    private LocalDateTime validStart;

    /**
     * 有效期结束
     */
    private LocalDateTime validEnd;

    /**
     * 状态：1未使用 2已使用 3已过期
     */
    private Integer status;

    /**
     * 使用时间
     */
    private LocalDateTime useTime;

    /**
     * 使用的订单ID
     */
    private Long orderId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
