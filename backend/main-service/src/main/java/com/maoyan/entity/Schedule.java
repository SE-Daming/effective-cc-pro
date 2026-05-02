package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 排片实体
 *
 * @author maoyan
 */
@Data
@TableName("schedule")
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 电影ID
     */
    private Long movieId;

    /**
     * 影院ID
     */
    private Long cinemaId;

    /**
     * 影厅ID
     */
    private Long hallId;

    /**
     * 放映日期
     */
    private LocalDate showDate;

    /**
     * 放映时间
     */
    private LocalTime showTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 清洁时间（分钟）
     */
    private Integer cleanDuration;

    /**
     * 票价
     */
    private BigDecimal price;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 已售座位数
     */
    private Integer soldSeats;

    /**
     * 状态：0取消 1正常
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
