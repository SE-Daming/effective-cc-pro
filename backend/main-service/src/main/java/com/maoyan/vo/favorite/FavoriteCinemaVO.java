package com.maoyan.vo.favorite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 收藏影院VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "收藏的影院")
public class FavoriteCinemaVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "收藏记录ID")
    private Long id;

    @Schema(description = "目标ID")
    private Long targetId;

    @Schema(description = "影院ID")
    private Long cinemaId;

    @Schema(description = "影院名称")
    private String cinemaName;

    @Schema(description = "影院地址")
    private String cinemaAddress;

    @Schema(description = "影院评分")
    private Double cinemaScore;

    @Schema(description = "影院状态：0禁用 1正常")
    private Integer cinemaStatus;

    @Schema(description = "收藏时间")
    private LocalDateTime createTime;
}
