package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 排片冲突检测响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "排片冲突检测响应")
public class ScheduleConflictCheckVO {

    @Schema(description = "是否存在冲突")
    private Boolean hasConflict;

    @Schema(description = "冲突的场次列表")
    private List<ConflictSchedule> conflictSchedules;

    /**
     * 冲突场次信息
     */
    @Data
    @Schema(description = "冲突场次信息")
    public static class ConflictSchedule {
        @Schema(description = "场次ID")
        private Long id;

        @Schema(description = "电影名称")
        private String movieTitle;

        @Schema(description = "放映时间")
        private String showTime;

        @Schema(description = "结束时间")
        private String endTime;
    }
}
