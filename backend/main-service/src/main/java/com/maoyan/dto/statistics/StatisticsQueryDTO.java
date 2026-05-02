package com.maoyan.dto.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 统计查询DTO
 *
 * @author maoyan
 */
@Data
@Schema(description = "统计查询参数")
public class StatisticsQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "开始日期")
    private LocalDate startDate;

    @Schema(description = "结束日期")
    private LocalDate endDate;

    @Schema(description = "类型：day-按日, month-按月", example = "day")
    private String type = "day";

    @Schema(description = "限制数量", example = "10")
    private Integer limit = 10;
}
