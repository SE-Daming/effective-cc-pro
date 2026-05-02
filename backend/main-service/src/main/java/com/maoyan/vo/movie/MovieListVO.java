package com.maoyan.vo.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 电影列表项 VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影列表项")
public class MovieListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影ID")
    private Long id;

    @Schema(description = "电影名称")
    private String title;

    @Schema(description = "海报URL")
    private String poster;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "时长（分钟）")
    private Integer duration;

    @Schema(description = "分类ID（逗号分隔）")
    private String categoryIds;

    @Schema(description = "分类名称（逗号分隔）")
    private String categoryNames;

    @Schema(description = "上映日期")
    private LocalDate releaseDate;

    @Schema(description = "状态：1即将上映 2上映中 3下架")
    private Integer status;

    @Schema(description = "想看人数")
    private Integer wantCount;

}
