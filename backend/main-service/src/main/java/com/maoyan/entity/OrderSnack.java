package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单卖品明细实体
 *
 * @author maoyan
 */
@Data
@TableName("order_snack")
public class OrderSnack implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 卖品ID
     */
    private Long snackId;

    /**
     * 卖品名称
     */
    private String snackName;

    /**
     * 规格
     */
    private String spec;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 小计
     */
    private BigDecimal totalPrice;

    /**
     * 取货码
     */
    private String pickCode;

    /**
     * 状态：1未取货 2已取货
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
