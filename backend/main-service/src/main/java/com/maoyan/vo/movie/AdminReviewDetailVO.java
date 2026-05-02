package com.maoyan.vo.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 管理端影评详情响应
 *
 * @author maoyan
 */
@Data
@Schema(description = "管理端影评详情响应")
public class AdminReviewDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "影评ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户手机号")
    private String userPhone;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "电影名称")
    private String movieTitle;

    @Schema(description = "电影海报")
    private String moviePoster;

    @Schema(description = "评分（1-10）")
    private Integer score;

    @Schema(description = "影评内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "状态：0隐藏 1显示")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
