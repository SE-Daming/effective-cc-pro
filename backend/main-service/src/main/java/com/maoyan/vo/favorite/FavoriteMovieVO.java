package com.maoyan.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏电影VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏的电影")
public class FavoriteMovieVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏记录ID")
    private Long id;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "电影标题")
    private String movieTitle;

    @Schema(description = "电影海报")
    private String moviePoster;

    @Schema(description = "电影评分")
    private Double movieScore;

    @Schema(description = "电影状态：1即将上映 2正在热映 3已下架")
    private Integer movieStatus;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
