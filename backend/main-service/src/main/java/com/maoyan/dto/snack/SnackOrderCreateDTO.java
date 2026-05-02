package com.maoyan.dto.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建卖品订单请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "创建卖品订单请求")
public class SnackOrderCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "影院ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "影院ID不能为空")
    private Long cinemaId;

    @Schema(description = "卖品项列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "卖品项列表不能为空")
    private List<SnackItem> items;

    @Schema(description = "用户优惠券ID")
    private Long userCouponId;

    @Data
    @Schema(description = "卖品项")
    public static class SnackItem implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "卖品ID", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "卖品ID不能为空")
        private Long snackId;

        @Schema(description = "规格", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "规格不能为空")
        private String spec;

        @Schema(description = "数量", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "数量不能为空")
        @Min(value = 1, message = "数量必须大于0")
        private Integer quantity;
    }
}
