package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 排片冲突检测请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "排片冲突检测请求")
public class ScheduleConflictCheckRequest {

    @Schema(description = "影厅ID", required = true)
    private Long hallId;

    @Schema(description = "放映日期", required = true)
    private LocalDate showDate;

    @Schema(description = "放映时间（格式：HH:mm）", required = true)
    private String showTime;

    @Schema(description = "电影时长（分钟）", required = true)
    private Integer duration;

    @Schema(description = "清洁时间（分钟）")
    private Integer cleanDuration = 15;

    @Schema(description = "排除的排片ID（编辑时使用）")
    private Long excludeId;

}
