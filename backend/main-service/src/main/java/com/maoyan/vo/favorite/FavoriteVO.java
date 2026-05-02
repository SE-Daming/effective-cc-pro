package com.maoyan.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏操作结果VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏操作结果")
public class FavoriteVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏记录ID")
    private Long id;

    @Schema(description = "类型：1电影 2影院")
    private Integer targetType;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
