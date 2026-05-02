package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.io.Serializable;

/**
 * 更新用户状态请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "更新用户状态请求")
public class UpdateUserStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态：0禁用 1正常", required = true)
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

}
