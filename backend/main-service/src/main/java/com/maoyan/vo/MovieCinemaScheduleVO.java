package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 电影排片影院列表响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影排片影院列表响应")
public class MovieCinemaScheduleVO {

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "影院地址")
    private String address;

    @Schema(description = "距离（km）")
    private Double distance;

    @Schema(description = "场次列表")
    private List<ScheduleSimpleVO> schedules;

    /**
     * 简单场次信息
     */
    @Data
    @Schema(description = "简单场次信息")
    public static class ScheduleSimpleVO {
        @Schema(description = "场次ID")
        private Long id;

        @Schema(description = "影厅名称")
        private String hallName;

        @Schema(description = "影厅类型")
        private String hallType;

        @Schema(description = "放映时间")
        private String showTime;

        @Schema(description = "票价")
        private BigDecimal price;

        @Schema(description = "已售座位数")
        private Integer soldSeats;

        @Schema(description = "总座位数")
        private Integer totalSeats;
    }
}
