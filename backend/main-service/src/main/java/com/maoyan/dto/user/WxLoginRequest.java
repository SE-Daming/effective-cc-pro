package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

/**
 * 微信登录请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "微信登录请求")
public class WxLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "微信登录code", required = true)
    @NotBlank(message = "code不能为空")
    private String code;

    @Schema(description = "加密数据")
    private String encryptedData;

    @Schema(description = "加密算法初始向量")
    private String iv;

}
