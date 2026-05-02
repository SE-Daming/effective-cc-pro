package com.maoyan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 影厅列表VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影厅列表VO")
public class HallListVO {

    @Schema(description = "影厅ID")
    private Long id;

    @Schema(description = "影厅名称")
    private String name;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "总座位数")
    private Integer totalSeats;

    @Schema(description = "状态")
    private Integer status;

}
