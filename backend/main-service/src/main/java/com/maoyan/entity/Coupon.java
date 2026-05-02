package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 优惠券模板实体
 *
 * @author maoyan
 */
@Data
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 优惠券名称
     */
    private String name;

    /**
     * 类型：1满减 2折扣 3立减 4兑换
     */
    private Integer type;

    /**
     * 优惠值（金额/折扣）
     */
    private BigDecimal value;

    /**
     * 最低消费金额
     */
    private BigDecimal minAmount;

    /**
     * 发放总量
     */
    private Integer totalCount;

    /**
     * 已使用数量
     */
    private Integer usedCount;

    /**
     * 已领取数量
     */
    private Integer receiveCount;

    /**
     * 每人限领数量
     */
    private Integer limitPerUser;

    /**
     * 有效天数
     */
    private Integer validDays;

    /**
     * 有效期开始
     */
    private LocalDate validStart;

    /**
     * 有效期结束
     */
    private LocalDate validEnd;

    /**
     * 适用范围：1全部 2指定电影 3指定影院
     */
    private Integer scope;

    /**
     * 适用ID列表（逗号分隔）
     */
    private String scopeIds;

    /**
     * 状态：0禁用 1启用
     */
    private Integer status;

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
