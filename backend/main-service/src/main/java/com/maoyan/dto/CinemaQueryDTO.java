package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 影院查询参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院查询参数")
public class CinemaQueryDTO {

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区域")
    private String district;

    @Schema(description = "品牌ID")
    private Integer brandId;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "用户经度")
    private BigDecimal longitude;

    @Schema(description = "用户纬度")
    private BigDecimal latitude;

    @Schema(description = "排序方式：distance/score")
    private String orderBy;

    @Schema(description = "页码", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "每页数量", defaultValue = "10")
    private Integer pageSize = 10;

}
