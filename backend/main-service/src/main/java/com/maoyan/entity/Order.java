package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 *
 * @author maoyan
 */
@Data
@TableName("`order`")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 类型：1电影票 2卖品 3组合
     */
    private Integer type;

    /**
     * 影院ID
     */
    private Long cinemaId;

    /**
     * 订单总额
     */
    private BigDecimal totalAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 使用的用户优惠券ID
     */
    private Long userCouponId;

    /**
     * 状态：1待支付 2已支付 3已出票 4已完成 5已取消 6退款中 7已退款
     */
    private Integer status;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 第三方交易号
     */
    private String payTradeNo;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退票手续费
     */
    private BigDecimal refundFee;

    /**
     * 退款时间
     */
    private LocalDateTime refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款审核人ID
     */
    private Integer refundAuditorId;

    /**
     * 退款审核时间
     */
    private LocalDateTime refundAuditTime;

    /**
     * 退款审核备注
     */
    private String refundAuditRemark;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDeleted;

}
