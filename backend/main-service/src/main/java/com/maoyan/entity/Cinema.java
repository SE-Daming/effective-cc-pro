package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 影院实体
 *
 * @author maoyan
 */
@Data
@TableName("cinema")
public class Cinema implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 影院名称
     */
    private String name;

    /**
     * 品牌ID
     */
    private Integer brandId;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 电话
     */
    private String phone;

    /**
     * 设施标签（逗号分隔）
     */
    private String facilities;

    /**
     * 影院介绍
     */
    private String description;

    /**
     * 图片URL（JSON数组）
     */
    private String images;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 状态：0关闭 1营业
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
