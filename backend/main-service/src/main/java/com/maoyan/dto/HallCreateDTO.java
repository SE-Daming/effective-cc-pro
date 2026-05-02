package com.maoyan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 影厅创建/更新参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "影厅创建/更新参数")
public class HallCreateDTO {

    @Schema(description = "影院ID")
    @NotNull(message = "影院ID不能为空")
    private Long cinemaId;

    @Schema(description = "影厅名称")
    @NotBlank(message = "影厅名称不能为空")
    private String name;

    @Schema(description = "类型：普通/IMAX/Dolby等")
    private String type;

    @Schema(description = "行数")
    @NotNull(message = "行数不能为空")
    private Integer rows;

    @Schema(description = "列数")
    @NotNull(message = "列数不能为空")
    private Integer cols;

    @Schema(description = "座位布局JSON")
    private String seatLayout;

}
