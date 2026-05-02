package com.maoyan.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收藏检查VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏检查结果")
public class FavoriteCheckVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "收藏记录ID（已收藏时有值）")
    private Long favoriteId;
}
