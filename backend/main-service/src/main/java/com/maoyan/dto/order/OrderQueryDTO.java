package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 订单查询条件
 *
 * @author maoyan
 */
@Data
@Schema(description = "订单查询条件")
public class OrderQueryDTO {

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "订单类型")
    private Integer type;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;

}
