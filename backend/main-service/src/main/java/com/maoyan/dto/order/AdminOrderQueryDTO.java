package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * B端订单查询条件
 *
 * @author maoyan
 */
@Data
@Schema(description = "B端订单查询条件")
public class AdminOrderQueryDTO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单类型")
    private Integer type;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

}
