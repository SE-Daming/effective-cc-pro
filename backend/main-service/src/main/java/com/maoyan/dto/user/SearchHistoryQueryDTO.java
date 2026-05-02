package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 搜索历史查询参数
 *
 * @author maoyan
 */
@Data
@Schema(description = "搜索历史查询参数")
public class SearchHistoryQueryDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型：1电影 2影院，不传查全部")
    private Integer type;

    @Schema(description = "数量限制，默认10")
    private Integer limit = 10;

}
