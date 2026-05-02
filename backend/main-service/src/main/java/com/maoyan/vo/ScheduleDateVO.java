package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 排片日期VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "排片日期VO")
public class ScheduleDateVO {

    @Schema(description = "日期")
    private String date;

    @Schema(description = "星期几")
    private String weekday;

    @Schema(description = "是否今天")
    private Boolean isToday;

    @Schema(description = "是否明天")
    private Boolean isTomorrow;

}
