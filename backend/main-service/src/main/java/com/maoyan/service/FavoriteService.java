package com.maoyan.service;

import com.maoyan.dto.favorite.FavoriteDTO;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.favorite.*;

/**
 * 收藏服务接口
 *
 * @author maoyan
 */
public interface FavoriteService {

    /**
     * 添加收藏
     *
     * @param dto    收藏请求
     * @param userId 用户ID
     * @return 收藏结果
     */
    FavoriteVO addFavorite(FavoriteDTO dto, Long userId);

    /**
     * 取消收藏
     *
     * @param dto    取消收藏请求
     * @param userId 用户ID
     */
    void removeFavorite(FavoriteDTO dto, Long userId);

    /**
     * 检查是否已收藏
     *
     * @param targetType 类型：1电影 2影院
     * @param targetId   目标ID
     * @param userId     用户ID
     * @return 检查结果
     */
    FavoriteCheckVO checkFavorite(Integer targetType, Long targetId, Long userId);

    /**
     * 获取收藏的电影列表
     *
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 电影列表
     */
    PageVO<FavoriteMovieVO> getFavoriteMovies(Long userId, Integer page, Integer pageSize);

    /**
     * 获取收藏的影院列表
     *
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页数量
     * @return 影院列表
     */
    PageVO<FavoriteCinemaVO> getFavoriteCinemas(Long userId, Integer page, Integer pageSize);

    /**
     * 获取收藏数量统计
     *
     * @param userId 用户ID
     * @return 收藏数量
     */
    FavoriteCountVO getFavoriteCount(Long userId);
}
