package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 座位实体
 *
 * @author maoyan
 */
@Data
@TableName("seat")
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 影厅ID
     */
    private Long hallId;

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 列号
     */
    private Integer colNum;

    /**
     * 座位号（如：F08）
     */
    private String seatNo;

    /**
     * 类型：1普通 2情侣 3VIP
     */
    private Integer seatType;

    /**
     * 状态：0禁用 1正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
