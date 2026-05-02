package com.maoyan.dto.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 影评发布参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "影评发布参数")
public class ReviewCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "电影ID")
    @NotNull(message = "电影ID不能为空")
    private Long movieId;

    @Schema(description = "评分（1-10）")
    @NotNull(message = "评分不能为空")
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 10, message = "评分最大为10")
    private Integer score;

    @Schema(description = "影评内容")
    @Size(max = 500, message = "影评内容不能超过500字")
    private String content;

}
