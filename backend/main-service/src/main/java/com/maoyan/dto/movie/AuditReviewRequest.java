package com.maoyan.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 影评审核请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "影评审核请求")
public class AuditReviewRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "状态：0隐藏 1显示")
    @NotNull(message = "状态不能为空")
    @Min(value = 0, message = "状态值无效")
    @Max(value = 1, message = "状态值无效")
    private Integer status;

}
