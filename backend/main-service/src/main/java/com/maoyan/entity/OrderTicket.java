package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单票务明细实体
 *
 * @author maoyan
 */
@Data
@TableName("order_ticket")
public class OrderTicket implements Serializable {

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
     * 场次ID
     */
    private Long scheduleId;

    /**
     * 座位ID
     */
    private Long seatId;

    /**
     * 座位号
     */
    private String seatNo;

    /**
     * 票价
     */
    private BigDecimal ticketPrice;

    /**
     * 取票码
     */
    private String pickCode;

    /**
     * 取票二维码
     */
    private String pickQrcode;

    /**
     * 状态：1未取票 2已取票
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
