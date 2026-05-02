package com.maoyan.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 电影新增/更新参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影新增/更新参数")
public class MovieSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影名称")
    @NotBlank(message = "电影名称不能为空")
    private String title;

    @Schema(description = "英文名称")
    private String englishTitle;

    @Schema(description = "海报URL")
    @NotBlank(message = "海报不能为空")
    private String poster;

    @Schema(description = "导演")
    private String director;

    @Schema(description = "主演（逗号分隔）")
    private String actors;

    @Schema(description = "时长（分钟）")
    private Integer duration;

    @Schema(description = "分类ID（逗号分隔）")
    private String categoryIds;

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

    @Schema(description = "评分（0-10）")
    private Double score;

    @Schema(description = "状态：1即将上映 2上映中 3下架")
    @NotNull(message = "状态不能为空")
    private Integer status;

    @Schema(description = "排序")
    private Integer sort;

}
