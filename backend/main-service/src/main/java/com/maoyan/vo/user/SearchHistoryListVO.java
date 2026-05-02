package com.maoyan.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 搜索历史列表响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "搜索历史列表响应")
public class SearchHistoryListVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索历史列表")
    private List<SearchHistoryVO> list;

}
