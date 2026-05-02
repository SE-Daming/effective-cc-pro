package com.maoyan.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 收藏数量统计VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏数量统计")
public class FavoriteCountVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏电影数量")
    private Long movieCount;

    @Schema(description = "收藏影院数量")
    private Long cinemaCount;

    @Schema(description = "收藏总数")
    private Long totalCount;
}
