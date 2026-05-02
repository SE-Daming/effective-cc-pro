package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 批量排片请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "批量排片请求")
public class ScheduleBatchCreateRequest {

    @NotNull(message = "电影ID不能为空")
    @Schema(description = "电影ID", required = true)
    private Long movieId;

    @NotNull(message = "影院ID不能为空")
    @Schema(description = "影院ID", required = true)
    private Long cinemaId;

    @NotNull(message = "影厅ID不能为空")
    @Schema(description = "影厅ID", required = true)
    private Long hallId;

    @NotEmpty(message = "放映日期不能为空")
    @Schema(description = "放映日期列表", required = true)
    private List<LocalDate> showDates;

    @NotEmpty(message = "放映时间不能为空")
    @Schema(description = "放映时间列表（格式：HH:mm）", required = true)
    private List<String> showTimes;

    @NotNull(message = "票价不能为空")
    @Schema(description = "票价", required = true)
    private BigDecimal price;

    @Schema(description = "清洁时间（分钟），默认15")
    private Integer cleanDuration = 15;

}
