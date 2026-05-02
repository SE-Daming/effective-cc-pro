package com.maoyan.vo.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 电影详情 VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影详情")
public class MovieDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影ID")
    private Long id;

    @Schema(description = "电影名称")
    private String title;

    @Schema(description = "英文名称")
    private String englishTitle;

    @Schema(description = "海报URL")
    private String poster;

    @Schema(description = "导演")
    private String director;

    @Schema(description = "主演（逗号分隔）")
    private String actors;

    @Schema(description = "时长（分钟）")
    private Integer duration;

    @Schema(description = "分类ID（逗号分隔）")
    private String categoryIds;

    @Schema(description = "分类名称（逗号分隔）")
    private String categoryNames;

    @Schema(description = "地区")
    private String region;

    @Schema(description = "语言")
    private String language;

    @Schema(description = "上映日期")
    private LocalDate releaseDate;

    @Schema(description = "下映日期")
    private LocalDate offDate;

    @Schema(description = "剧情简介")
    private String synopsis;

    @Schema(description = "预告片URL")
    private String trailerUrl;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "评分人数")
    private Integer scoreCount;

    @Schema(description = "状态：1即将上映 2上映中 3下架")
    private Integer status;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

    @Schema(description = "想看人数")
    private Integer wantCount;

}
