package com.maoyan.vo.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 卖品分类VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品分类")
public class SnackCategoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    private Integer id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "图标URL")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;
}
