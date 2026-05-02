package com.maoyan.service;

import com.maoyan.dto.statistics.StatisticsQueryDTO;
import com.maoyan.vo.statistics.*;

import java.util.List;

/**
 * 数据统计服务接口
 *
 * @author maoyan
 */
public interface StatisticsService {

    /**
     * 获取销售统计概览
     *
     * @param queryDTO 查询参数
     * @return 统计概览
     */
    StatisticsOverviewVO getOverview(StatisticsQueryDTO queryDTO);

    /**
     * 获取销售趋势
     *
     * @param queryDTO 查询参数
     * @return 销售趋势列表
     */
    List<SalesTrendVO> getSalesTrend(StatisticsQueryDTO queryDTO);

    /**
     * 获取电影票房排行
     *
     * @param queryDTO 查询参数
     * @return 排行列表
     */
    List<MovieRankingVO> getMovieRanking(StatisticsQueryDTO queryDTO);

    /**
     * 获取影院销售排行
     *
     * @param queryDTO 查询参数
     * @return 排行列表
     */
    List<CinemaRankingVO> getCinemaRanking(StatisticsQueryDTO queryDTO);
}
