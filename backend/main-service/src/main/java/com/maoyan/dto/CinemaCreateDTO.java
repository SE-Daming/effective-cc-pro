package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 影院创建/更新参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院创建/更新参数")
public class CinemaCreateDTO {

    @Schema(description = "影院名称")
    @NotBlank(message = "影院名称不能为空")
    private String name;

    @Schema(description = "品牌ID")
    private Integer brandId;

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

    @Schema(description = "设施标签（逗号分隔）")
    private String facilities;

    @Schema(description = "影院介绍")
    private String description;

    @Schema(description = "图片URL列表")
    private List<String> images;

}
