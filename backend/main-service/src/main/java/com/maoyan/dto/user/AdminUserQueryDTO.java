package com.maoyan.dto.user;

import com.maoyan.dto.PageDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 管理端用户查询参数
 *
 * @author maoyan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "管理端用户查询参数")
public class AdminUserQueryDTO extends PageDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "搜索关键词（昵称/手机号）")
    private String keyword;

    @Schema(description = "状态：0禁用 1正常")
    private Integer status;

}
