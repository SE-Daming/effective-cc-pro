package com.maoyan.vo.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 卖品取货码响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品取货码响应")
public class SnackPickCodeVO {

    @Schema(description = "取货码")
    private String pickCode;

    @Schema(description = "卖品列表")
    private List<SnackItem> snacks;

    /**
     * 卖品项
     */
    @Data
    @Schema(description = "卖品项")
    public static class SnackItem {
        @Schema(description = "卖品名称")
        private String name;

        @Schema(description = "规格")
        private String spec;

        @Schema(description = "数量")
        private Integer quantity;
    }

}
