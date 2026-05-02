package com.maoyan.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 清空搜索历史请求
 *
 * @author maoyan
 */
@Data
@Schema(description = "清空搜索历史请求")
public class ClearSearchHistoryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "类型：1电影 2影院，不传清空全部")
    private Integer type;

}
