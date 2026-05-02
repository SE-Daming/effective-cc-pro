package com.maoyan.dto.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * 收藏操作DTO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏操作请求")
public class FavoriteDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型：1电影 2影院", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类型不能为空")
    private Integer targetType;

    @Schema(description = "目标ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "目标ID不能为空")
    private Long targetId;
}
