package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 电影实体
 *
 * @author maoyan
 */
@Data
@TableName("movie")
public class Movie implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 电影名称
     */
    private String title;

    /**
     * 英文名称
     */
    private String englishTitle;

    /**
     * 海报URL
     */
    private String poster;

    /**
     * 导演
     */
    private String director;

    /**
     * 主演（逗号分隔）
     */
    private String actors;

    /**
     * 时长（分钟）
     */
    private Integer duration;

    /**
     * 分类ID（逗号分隔）
     */
    private String categoryIds;

    /**
     * 地区
     */
    private String region;

    /**
     * 语言
     */
    private String language;

    /**
     * 上映日期
     */
    private LocalDate releaseDate;

    /**
     * 下映日期
     */
    private LocalDate offDate;

    /**
     * 剧情简介
     */
    private String synopsis;

    /**
     * 预告片URL
     */
    private String trailerUrl;

    /**
     * 评分（0-10）
     */
    private BigDecimal score;

    /**
     * 评分人数
     */
    private Integer scoreCount;

    /**
     * 状态：1即将上映 2上映中 3下架
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sort;

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
