package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户信息请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "更新用户信息请求")
public class UserProfileUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "昵称")
    @Size(min = 1, max = 32, message = "昵称长度为1-32个字符")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "性别：0未知 1男 2女")
    @Min(value = 0, message = "性别值无效")
    @Max(value = 2, message = "性别值无效")
    private Integer gender;

}
