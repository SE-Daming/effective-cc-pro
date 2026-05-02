package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 排片详情响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "排片详情响应")
public class ScheduleDetailVO {

    @Schema(description = "场次ID")
    private Long id;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "电影名称")
    private String movieTitle;

    @Schema(description = "电影海报")
    private String moviePoster;

    @Schema(description = "电影时长（分钟）")
    private Integer movieDuration;

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "影厅ID")
    private Long hallId;

    @Schema(description = "影厅名称")
    private String hallName;

    @Schema(description = "影厅类型")
    private String hallType;

    @Schema(description = "放映日期")
    private LocalDate showDate;

    @Schema(description = "放映时间")
    private String showTime;

    @Schema(description = "结束时间")
    private String endTime;

    @Schema(description = "票价")
    private BigDecimal price;

    @Schema(description = "总座位数")
    private Integer totalSeats;

    @Schema(description = "已售座位数")
    private Integer soldSeats;

    @Schema(description = "状态：0取消 1正常")
    private Integer status;

}
