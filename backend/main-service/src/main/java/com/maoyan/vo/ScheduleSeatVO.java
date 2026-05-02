package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 场次座位图响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "场次座位图响应")
public class ScheduleSeatVO {

    @Schema(description = "影厅信息")
    private HallInfo hallInfo;

    @Schema(description = "电影信息")
    private MovieInfo movieInfo;

    @Schema(description = "场次信息")
    private ScheduleInfo scheduleInfo;

    @Schema(description = "座位列表")
    private List<SeatInfo> seats;

    @Schema(description = "最大可选数量")
    private Integer maxSelect = 4;

    /**
     * 影厅信息
     */
    @Data
    @Schema(description = "影厅信息")
    public static class HallInfo {
        @Schema(description = "影厅ID")
        private Long id;

        @Schema(description = "影厅名称")
        private String name;

        @Schema(description = "影厅类型")
        private String type;

        @Schema(description = "行数")
        private Integer rows;

        @Schema(description = "列数")
        private Integer cols;
    }

    /**
     * 电影信息
     */
    @Data
    @Schema(description = "电影信息")
    public static class MovieInfo {
        @Schema(description = "电影ID")
        private Long id;

        @Schema(description = "电影名称")
        private String title;

        @Schema(description = "电影海报")
        private String poster;

        @Schema(description = "电影时长（分钟）")
        private Integer duration;
    }

    /**
     * 场次信息
     */
    @Data
    @Schema(description = "场次信息")
    public static class ScheduleInfo {
        @Schema(description = "场次ID")
        private Long id;

        @Schema(description = "放映日期")
        private LocalDate showDate;

        @Schema(description = "放映时间")
        private String showTime;

        @Schema(description = "票价")
        private BigDecimal price;
    }

    /**
     * 座位信息
     */
    @Data
    @Schema(description = "座位信息")
    public static class SeatInfo {
        @Schema(description = "座位ID")
        private Long id;

        @Schema(description = "行号")
        private Integer rowNum;

        @Schema(description = "列号")
        private Integer colNum;

        @Schema(description = "座位号")
        private String seatNo;

        @Schema(description = "座位类型：1普通 2情侣 3VIP")
        private Integer seatType;

        @Schema(description = "座位状态：0禁用 1正常")
        private Integer status;

        @Schema(description = "锁定状态：0可选 1已锁定 2已售")
        private Integer lockStatus;
    }
}
