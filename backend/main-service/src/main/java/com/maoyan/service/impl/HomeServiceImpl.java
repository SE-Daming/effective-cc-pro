package com.maoyan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maoyan.entity.Banner;
import com.maoyan.entity.Movie;
import com.maoyan.entity.MovieCategory;
import com.maoyan.mapper.BannerMapper;
import com.maoyan.mapper.MovieCategoryMapper;
import com.maoyan.mapper.MovieMapper;
import com.maoyan.service.HomeService;
import com.maoyan.vo.home.BannerVO;
import com.maoyan.vo.home.HotMovieVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final BannerMapper bannerMapper;
    private final MovieMapper movieMapper;
    private final MovieCategoryMapper movieCategoryMapper;

    @Override
    public List<BannerVO> getBanners() {
        LocalDateTime now = LocalDateTime.now();
        LambdaQueryWrapper<Banner> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Banner::getStatus, 1)
                .eq(Banner::getPosition, 1) // 首页位置
                .le(Banner::getStartTime, now)
                .ge(Banner::getEndTime, now)
                .orderByAsc(Banner::getSort);
        List<Banner> banners = bannerMapper.selectList(wrapper);
        return banners.stream()
                .map(this::convertToBannerVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<HotMovieVO> getHotMovies(Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10;
        }
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 2) // 正在热映
                .orderByDesc(Movie::getScore)
                .last("LIMIT " + limit);
        List<Movie> movies = movieMapper.selectList(wrapper);

        // 获取分类名称映射
        Map<Integer, String> categoryNameMap = getCategoryNameMap();

        return movies.stream()
                .map(movie -> convertToHotMovieVO(movie, categoryNameMap))
                .collect(Collectors.toList());
    }

    // ===== 私有方法 =====

    private BannerVO convertToBannerVO(Banner banner) {
        BannerVO vo = new BannerVO();
        BeanUtils.copyProperties(banner, vo);
        return vo;
    }

    private HotMovieVO convertToHotMovieVO(Movie movie, Map<Integer, String> categoryNameMap) {
        HotMovieVO vo = new HotMovieVO();
        vo.setId(movie.getId());
        vo.setTitle(movie.getTitle());
        vo.setPoster(movie.getPoster());
        vo.setScore(movie.getScore() != null ? movie.getScore().doubleValue() : null);
        vo.setActors(movie.getActors());
        vo.setReleaseDate(movie.getReleaseDate() != null ? movie.getReleaseDate().toString() : null);
        vo.setStatus(movie.getStatus());

        // 解析分类
        if (StrUtil.isNotBlank(movie.getCategoryIds())) {
            List<String> categories = Arrays.stream(movie.getCategoryIds().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .map(categoryNameMap::get)
                    .filter(name -> name != null)
                    .collect(Collectors.toList());
            vo.setCategories(categories);
        }

        return vo;
    }

    private Map<Integer, String> getCategoryNameMap() {
        List<MovieCategory> categories = movieCategoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(MovieCategory::getId, MovieCategory::getName));
    }
}
