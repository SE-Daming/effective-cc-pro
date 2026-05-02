package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 附近影院查询参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "附近影院查询参数")
public class NearbyCinemaQueryDTO {

    @Schema(description = "用户经度", required = true)
    private BigDecimal longitude;

    @Schema(description = "用户纬度", required = true)
    private BigDecimal latitude;

    @Schema(description = "搜索半径（km），默认5")
    private Integer radius = 5;

    @Schema(description = "数量限制，默认10")
    private Integer limit = 10;

}
