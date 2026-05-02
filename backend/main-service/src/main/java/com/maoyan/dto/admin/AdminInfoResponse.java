package com.maoyan.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理员信息响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "管理员信息响应")
public class AdminInfoResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "管理员ID")
    private Integer id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "角色ID")
    private Integer roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "权限列表")
    private Object permissions;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginTime;

}
