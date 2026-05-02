package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 影院详情VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院详情VO")
public class CinemaDetailVO {

    @Schema(description = "影院ID")
    private Long id;

    @Schema(description = "影院名称")
    private String name;

    @Schema(description = "品牌ID")
    private Integer brandId;

    @Schema(description = "品牌名称")
    private String brandName;

    @Schema(description = "省")
    private String province;

    @Schema(description = "市")
    private String city;

    @Schema(description = "区")
    private String district;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "设施标签")
    private String facilities;

    @Schema(description = "影院介绍")
    private String description;

    @Schema(description = "图片列表")
    private List<String> images;

    @Schema(description = "评分")
    private BigDecimal score;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "是否已收藏")
    private Boolean isFavorite;

}
