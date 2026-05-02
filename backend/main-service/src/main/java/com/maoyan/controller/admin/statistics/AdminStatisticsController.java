package com.maoyan.controller.admin.statistics;

import com.maoyan.common.Result;
import com.maoyan.dto.statistics.StatisticsQueryDTO;
import com.maoyan.service.StatisticsService;
import com.maoyan.vo.statistics.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B端 - 数据统计接口
 *
 * @author maoyan
 */
@Tag(name = "B端-数据统计", description = "销售统计、排行榜等接口")
@RestController
@RequestMapping("/admin/statistics")
@RequiredArgsConstructor
public class AdminStatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "销售统计概览")
    @GetMapping("/overview")
    public Result<StatisticsOverviewVO> getOverview(StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getOverview(queryDTO));
    }

    @Operation(summary = "销售趋势")
    @GetMapping("/trend")
    public Result<List<SalesTrendVO>> getSalesTrend(StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getSalesTrend(queryDTO));
    }

    @Operation(summary = "电影票房排行")
    @GetMapping("/movie-ranking")
    public Result<List<MovieRankingVO>> getMovieRanking(StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getMovieRanking(queryDTO));
    }

    @Operation(summary = "影院销售排行")
    @GetMapping("/cinema-ranking")
    public Result<List<CinemaRankingVO>> getCinemaRanking(StatisticsQueryDTO queryDTO) {
        return Result.success(statisticsService.getCinemaRanking(queryDTO));
    }
}
