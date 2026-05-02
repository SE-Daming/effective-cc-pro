package com.maoyan.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员操作日志实体
 *
 * @author maoyan
 */
@Data
@TableName("admin_operation_log")
public class AdminOperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 管理员ID
     */
    private Integer adminId;

    /**
     * 管理员姓名
     */
    private String adminName;

    /**
     * 模块
     */
    private String module;

    /**
     * 操作
     */
    private String action;

    /**
     * 操作对象
     */
    private String target;

    /**
     * 操作内容
     */
    private String content;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
