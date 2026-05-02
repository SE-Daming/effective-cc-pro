package com.maoyan.dto.snack;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 卖品查询DTO
 *
 * @author maoyan
 */
@Data
@Schema(description = "卖品查询参数")
public class SnackQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "分类ID")
    private Integer categoryId;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", example = "10")
    private Integer pageSize = 10;
}
