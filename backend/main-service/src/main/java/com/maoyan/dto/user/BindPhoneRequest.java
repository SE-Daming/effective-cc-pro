package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

/**
 * 绑定手机号请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "绑定手机号请求")
public class BindPhoneRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "手机号", required = true)
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @Schema(description = "验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String code;

}
