package com.maoyan.vo.home;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * Banner VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "轮播图")
public class BannerVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "Banner ID")
    private Integer id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "图片URL")
    private String image;

    @Schema(description = "链接类型：1电影 2活动 3外链")
    private Integer linkType;

    @Schema(description = "关联ID")
    private Long linkId;

    @Schema(description = "跳转链接")
    private String linkUrl;
}
