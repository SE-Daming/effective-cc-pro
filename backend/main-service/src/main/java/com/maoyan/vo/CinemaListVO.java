package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 影院列表VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院列表VO")
public class CinemaListVO {

    @Schema(description = "影院ID")
    private Long id;

    @Schema(description = "影院名称")
    private String name;

    @Schema(description = "品牌ID")
    private Integer brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "距离（km）")
    private BigDecimal distance;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "设施标签")
    private String facilities;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "状态")
    private Integer status;

}
