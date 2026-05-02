package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 管理端影院查询参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "管理端影院查询参数")
public class AdminCinemaQueryDTO {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "品牌ID")
    private Integer brandId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "页码", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;

}
