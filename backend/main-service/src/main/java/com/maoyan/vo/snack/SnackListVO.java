package com.maoyan.vo.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 卖品列表VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品列表项")
public class SnackListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "卖品ID")
    private Long id;

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "分类名称")
    private String categoryName;

    @Schema(description = "卖品名称")
    private String name;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "价格")
    private BigDecimal price;

    @Schema(description = "规格列表")
    private List<SnackSpecVO> specs;

    @Schema(description = "库存")
    private Integer stock;

    @Schema(description = "销量")
    private Integer sales;

    @Schema(description = "状态：0下架 1上架")
    private Integer status;

    @Data
    @Schema(description = "卖品规格")
    public static class SnackSpecVO implements Serializable {

        private static final long serialVersionUID = 1L;

        @Schema(description = "规格名称")
        private String name;

        @Schema(description = "规格价格")
        private BigDecimal price;

        @Schema(description = "规格库存")
        private Integer stock;
    }
}
