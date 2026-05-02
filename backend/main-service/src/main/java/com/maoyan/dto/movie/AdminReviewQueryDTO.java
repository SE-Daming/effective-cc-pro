package com.maoyan.dto.movie;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理端影评查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理端影评查询参数")
public class AdminReviewQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "状态：0隐藏 1显示")
    private Integer status;

}
