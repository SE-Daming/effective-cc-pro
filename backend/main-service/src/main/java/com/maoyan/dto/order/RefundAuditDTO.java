package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 退款审核请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "退款审核请求")
public class RefundAuditDTO {

    @Schema(description = "是否同意", required = true)
    private Boolean approve;

    @Schema(description = "审核备注")
    private String remark;

}
