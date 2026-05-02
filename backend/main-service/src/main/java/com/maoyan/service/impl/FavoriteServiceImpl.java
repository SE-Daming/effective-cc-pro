package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.favorite.FavoriteDTO;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.FavoriteService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.favorite.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 收藏服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserFavoriteMapper userFavoriteMapper;
    private final MovieMapper movieMapper;
    private final CinemaMapper cinemaMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoriteVO addFavorite(FavoriteDTO dto, Long userId) {
        // 检查是否已收藏
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, dto.getTargetType())
                .eq(UserFavorite::getTargetId, dto.getTargetId());
        UserFavorite existing = userFavoriteMapper.selectOne(wrapper);
        if (existing != null) {
            throw new BusinessException("已收藏");
        }

        // 验证目标是否存在
        validateTarget(dto.getTargetType(), dto.getTargetId());

        // 创建收藏
        UserFavorite favorite = new UserFavorite();
        favorite.setUserId(userId);
        favorite.setTargetType(dto.getTargetType());
        favorite.setTargetId(dto.getTargetId());
        userFavoriteMapper.insert(favorite);

        FavoriteVO vo = new FavoriteVO();
        BeanUtils.copyProperties(favorite, vo);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFavorite(FavoriteDTO dto, Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, dto.getTargetType())
                .eq(UserFavorite::getTargetId, dto.getTargetId());
        int deleted = userFavoriteMapper.delete(wrapper);
        if (deleted == 0) {
            throw new BusinessException("未收藏");
        }
    }

    @Override
    public FavoriteCheckVO checkFavorite(Integer targetType, Long targetId, Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, targetType)
                .eq(UserFavorite::getTargetId, targetId);
        UserFavorite favorite = userFavoriteMapper.selectOne(wrapper);

        FavoriteCheckVO vo = new FavoriteCheckVO();
        if (favorite != null) {
            vo.setIsFavorite(true);
            vo.setFavoriteId(favorite.getId());
        } else {
            vo.setIsFavorite(false);
        }
        return vo;
    }

    @Override
    public PageVO<FavoriteMovieVO> getFavoriteMovies(Long userId, Integer page, Integer pageSize) {
        Page<UserFavorite> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, 1) // 电影
                .orderByDesc(UserFavorite::getCreateTime);
        Page<UserFavorite> favoritePage = userFavoriteMapper.selectPage(pageParam, wrapper);

        List<FavoriteMovieVO> list = favoritePage.getRecords().stream()
                .map(this::convertToFavoriteMovieVO)
                .collect(Collectors.toList());

        return PageVO.of(list, favoritePage.getTotal(), (int) favoritePage.getCurrent(), (int) favoritePage.getSize());
    }

    @Override
    public PageVO<FavoriteCinemaVO> getFavoriteCinemas(Long userId, Integer page, Integer pageSize) {
        Page<UserFavorite> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, 2) // 影院
                .orderByDesc(UserFavorite::getCreateTime);
        Page<UserFavorite> favoritePage = userFavoriteMapper.selectPage(pageParam, wrapper);

        List<FavoriteCinemaVO> list = favoritePage.getRecords().stream()
                .map(this::convertToFavoriteCinemaVO)
                .collect(Collectors.toList());

        return PageVO.of(list, favoritePage.getTotal(), (int) favoritePage.getCurrent(), (int) favoritePage.getSize());
    }

    @Override
    public FavoriteCountVO getFavoriteCount(Long userId) {
        LambdaQueryWrapper<UserFavorite> movieWrapper = new LambdaQueryWrapper<>();
        movieWrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, 1);
        Long movieCount = userFavoriteMapper.selectCount(movieWrapper);

        LambdaQueryWrapper<UserFavorite> cinemaWrapper = new LambdaQueryWrapper<>();
        cinemaWrapper.eq(UserFavorite::getUserId, userId)
                .eq(UserFavorite::getTargetType, 2);
        Long cinemaCount = userFavoriteMapper.selectCount(cinemaWrapper);

        FavoriteCountVO vo = new FavoriteCountVO();
        vo.setMovieCount(movieCount);
        vo.setCinemaCount(cinemaCount);
        vo.setTotalCount(movieCount + cinemaCount);
        return vo;
    }

    // ===== 私有方法 =====

    private void validateTarget(Integer targetType, Long targetId) {
        if (targetType == 1) {
            // 电影
            Movie movie = movieMapper.selectById(targetId);
            if (movie == null) {
                throw new BusinessException("电影不存在");
            }
        } else if (targetType == 2) {
            // 影院
            Cinema cinema = cinemaMapper.selectById(targetId);
            if (cinema == null) {
                throw new BusinessException("影院不存在");
            }
        } else {
            throw new BusinessException("无效的收藏类型");
        }
    }

    private FavoriteMovieVO convertToFavoriteMovieVO(UserFavorite favorite) {
        FavoriteMovieVO vo = new FavoriteMovieVO();
        vo.setId(favorite.getId());
        vo.setTargetId(favorite.getTargetId());
        vo.setCreateTime(favorite.getCreateTime());

        Movie movie = movieMapper.selectById(favorite.getTargetId());
        if (movie != null) {
            vo.setMovieId(movie.getId());
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
            vo.setMovieScore(movie.getScore() != null ? movie.getScore().doubleValue() : null);
            vo.setMovieStatus(movie.getStatus());
        }

        return vo;
    }

    private FavoriteCinemaVO convertToFavoriteCinemaVO(UserFavorite favorite) {
        FavoriteCinemaVO vo = new FavoriteCinemaVO();
        vo.setId(favorite.getId());
        vo.setTargetId(favorite.getTargetId());
        vo.setCreateTime(favorite.getCreateTime());

        Cinema cinema = cinemaMapper.selectById(favorite.getTargetId());
        if (cinema != null) {
            vo.setCinemaId(cinema.getId());
            vo.setCinemaName(cinema.getName());
            vo.setCinemaAddress(cinema.getAddress());
            vo.setCinemaScore(cinema.getScore() != null ? cinema.getScore().doubleValue() : null);
            vo.setCinemaStatus(cinema.getStatus());
        }

        return vo;
    }
}
