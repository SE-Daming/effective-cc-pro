package com.maoyan.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 创建组合订单请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "创建组合订单请求")
public class ComboOrderCreateDTO {

    @Schema(description = "锁定ID", required = true)
    private String lockId;

    @Schema(description = "订单类型：3组合", required = true)
    private Integer type;

    @Schema(description = "卖品列表")
    private List<SnackItem> snacks;

    @Schema(description = "用户优惠券ID")
    private Long userCouponId;

    /**
     * 卖品项
     */
    @Data
    @Schema(description = "卖品项")
    public static class SnackItem {
        @Schema(description = "卖品ID", required = true)
        private Long snackId;

        @Schema(description = "规格")
        private String spec;

        @Schema(description = "数量", required = true)
        private Integer quantity;
    }

}
