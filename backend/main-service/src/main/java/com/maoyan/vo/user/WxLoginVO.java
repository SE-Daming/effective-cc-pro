package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信登录响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "微信登录响应")
public class WxLoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Token")
    private String token;

    @Schema(description = "用户信息")
    private UserInfoVO userInfo;

    @Schema(description = "是否新用户")
    private Boolean isNewUser;

}
