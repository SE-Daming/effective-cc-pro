package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 影厅实体
 *
 * @author maoyan
 */
@Data
@TableName("hall")
public class Hall implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 影院ID
     */
    private Long cinemaId;

    /**
     * 影厅名称
     */
    private String name;

    /**
     * 类型：普通/IMAX/Dolby等
     */
    private String type;

    /**
     * 行数
     */
    private Integer rows;

    /**
     * 列数
     */
    private Integer cols;

    /**
     * 总座位数
     */
    private Integer totalSeats;

    /**
     * 座位布局JSON
     */
    private String seatLayout;

    /**
     * 状态：0关闭 1正常
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
