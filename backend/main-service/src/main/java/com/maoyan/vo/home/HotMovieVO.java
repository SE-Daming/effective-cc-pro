package com.maoyan.vo.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 热门电影推荐VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "热门电影推荐")
public class HotMovieVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影ID")
    private Long id;

    @Schema(description = "电影标题")
    private String title;

    @Schema(description = "海报URL")
    private String poster;

    @Schema(description = "评分")
    private Double score;

    @Schema(description = "电影类型（分类名称列表）")
    private List<String> categories;

    @Schema(description = "主演")
    private String actors;

    @Schema(description = "上映日期")
    private String releaseDate;

    @Schema(description = "电影状态：1即将上映 2正在热映")
    private Integer status;
}
