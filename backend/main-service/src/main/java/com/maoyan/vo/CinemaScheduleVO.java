package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 影院排片VO（按电影分组）
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院排片VO")
public class CinemaScheduleVO {

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "电影名称")
    private String movieTitle;

    @Schema(description = "电影海报")
    private String moviePoster;

    @Schema(description = "电影评分")
    private BigDecimal score;

    @Schema(description = "电影时长（分钟）")
    private Integer duration;

    @Schema(description = "场次列表")
    private List<ScheduleItemVO> schedules;

}
