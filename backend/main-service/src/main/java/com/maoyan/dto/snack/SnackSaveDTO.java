package com.maoyan.dto.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 卖品保存DTO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品保存请求")
public class SnackSaveDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分类ID不能为空")
    private Integer categoryId;

    @Schema(description = "卖品名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "卖品名称不能为空")
    private String name;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "价格", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "价格不能为空")
    private BigDecimal price;

    @Schema(description = "规格列表JSON")
    private List<SnackSpec> specs;

    @Schema(description = "库存")
    private Integer stock = 0;

    @Schema(description = "状态：0下架 1上架")
    private Integer status = 1;

    @Schema(description = "排序")
    private Integer sort = 0;

    @Data
    @Schema(description = "卖品规格")
    public static class SnackSpec implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "规格名称")
        private String name;

        @Schema(description = "规格价格")
        private BigDecimal price;

        @Schema(description = "规格库存")
        private Integer stock;
    }
}
