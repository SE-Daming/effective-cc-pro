package com.maoyan.dto.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 登录响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "登录响应")
public class AdminLoginResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "管理员信息")
    private AdminInfoResponse adminInfo;

}
