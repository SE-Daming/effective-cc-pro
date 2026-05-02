package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "搜索历史响应")
public class SearchHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "类型：1电影 2影院")
    private Integer type;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
