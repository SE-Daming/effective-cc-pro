package com.maoyan.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 管理员登录请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "管理员登录请求")
public class AdminLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;

}
