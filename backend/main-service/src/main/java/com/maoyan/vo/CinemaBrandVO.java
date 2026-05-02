package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 影院品牌VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影院品牌VO")
public class CinemaBrandVO {

    @Schema(description = "品牌ID")
    private Integer id;

    @Schema(description = "品牌名称")
    private String name;

    @Schema(description = "品牌Logo")
    private String logo;

    @Schema(description = "排序")
    private Integer sort;

}
