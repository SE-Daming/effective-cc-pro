package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 退票申请请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "退票申请请求")
public class RefundApplyDTO {

    @Schema(description = "退票原因", required = true)
    private String reason;

}
