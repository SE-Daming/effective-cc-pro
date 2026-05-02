package com.maoyan.dto.movie;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 电影查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "电影查询参数")
public class MovieQueryDTO extends PageDTO {

    @Schema(description = "搜索关键词")
    private String keyword;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "电影状态：1即将上映 2上映中 3下架")
    private Integer status;

}
