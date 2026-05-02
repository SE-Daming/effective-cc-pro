package com.maoyan.vo.movie;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 影评 VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "影评")
public class ReviewVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "影评ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户昵称")
    private String nickname;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "评分（1-10）")
    private Integer score;

    @Schema(description = "影评内容")
    private String content;

    @Schema(description = "点赞数")
    private Integer likeCount;

    @Schema(description = "当前用户是否已点赞")
    private Boolean isLiked;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
