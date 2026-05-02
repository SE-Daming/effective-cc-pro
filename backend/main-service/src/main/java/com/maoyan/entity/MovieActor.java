package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电影演员实体
 *
 * @author maoyan
 */
@Data
@TableName("movie_actor")
public class MovieActor implements Serializable {

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
     * 演员姓名
     */
    private String name;

    /**
     * 角色：主演/配角/导演等
     */
    private String role;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
