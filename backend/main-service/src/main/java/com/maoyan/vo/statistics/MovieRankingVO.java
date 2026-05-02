package com.maoyan.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 电影票房排行VO
 *
 * @author maoyan
 */
@Data
@Schema(description = "电影票房排行")
public class MovieRankingVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "排名")
    private Integer rank;

    @Schema(description = "电影ID")
    private Long movieId;

    @Schema(description = "电影标题")
    private String movieTitle;

    @Schema(description = "电影海报")
    private String moviePoster;

    @Schema(description = "票房金额")
    private BigDecimal boxOffice;

    @Schema(description = "售票数量")
    private Long ticketCount;

    @Schema(description = "场次数量")
    private Long scheduleCount;
}
