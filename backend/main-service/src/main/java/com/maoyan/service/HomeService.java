package com.maoyan.service;

import com.maoyan.vo.home.BannerVO;
import com.maoyan.vo.home.HotMovieVO;

import java.util.List;

/**
 * 首页服务接口
 *
 * @author maoyan
 */
public interface HomeService {

    /**
     * 获取Banner列表
     *
     * @return Banner列表
     */
    List<BannerVO> getBanners();

    /**
     * 获取热门电影推荐
     *
     * @param limit 限制数量
     * @return 热门电影列表
     */
    List<HotMovieVO> getHotMovies(Integer limit);
}
