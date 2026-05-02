package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 场次项VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "场次项VO")
public class ScheduleItemVO {

    @Schema(description = "场次ID")
    private Long id;

    @Schema(description = "影厅ID")
    private Long hallId;

    @Schema(description = "影厅名称")
    private String hallName;

    @Schema(description = "影厅类型")
    private String hallType;

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

    @Schema(description = "状态")
    private Integer status;

}
