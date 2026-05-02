package com.maoyan.vo.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 电影演员 VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影演员")
public class MovieActorVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "演员姓名")
    private String name;

    @Schema(description = "角色：主演/配角/导演等")
    private String role;

    @Schema(description = "排序")
    private Integer sort;

}
