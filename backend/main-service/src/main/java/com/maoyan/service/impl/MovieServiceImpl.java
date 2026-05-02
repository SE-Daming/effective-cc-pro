package com.maoyan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.movie.AdminReviewQueryDTO;
import com.maoyan.dto.movie.AuditReviewRequest;
import com.maoyan.dto.movie.MovieQueryDTO;
import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.dto.movie.ReviewCreateDTO;
import com.maoyan.dto.movie.ReviewQueryDTO;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.MovieCategoryService;
import com.maoyan.service.MovieService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.movie.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 电影服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private final MovieMapper movieMapper;
    private final MovieCategoryMapper movieCategoryMapper;
    private final MovieActorMapper movieActorMapper;
    private final MovieReviewMapper movieReviewMapper;
    private final UserFavoriteMapper userFavoriteMapper;
    private final UserMapper userMapper;
    private final MovieCategoryService movieCategoryService;

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    // Redis key 前缀
    private static final String REVIEW_LIKE_KEY = "review:like:";
    private static final String REVIEW_LIKE_USER_KEY = "review:like:user:";

    @Override
    public PageVO<MovieListVO> getNowPlaying(MovieQueryDTO queryDTO) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 2) // 上映中
                .orderByDesc(Movie::getScore)
                .orderByDesc(Movie::getReleaseDate);

        Page<Movie> page = movieMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        return toMovieListPageVO(page);
    }

    @Override
    public PageVO<MovieListVO> getComingSoon(MovieQueryDTO queryDTO) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Movie::getStatus, 1) // 即将上映
                .orderByAsc(Movie::getReleaseDate);

        Page<Movie> page = movieMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        return toMovieListPageVO(page);
    }

    @Override
    public PageVO<MovieListVO> searchMovies(MovieQueryDTO queryDTO) {
        if (StrUtil.isBlank(queryDTO.getKeyword())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "搜索关键词不能为空");
        }

        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Movie::getTitle, queryDTO.getKeyword())
                .or()
                .like(Movie::getEnglishTitle, queryDTO.getKeyword())
                .or()
                .like(Movie::getDirector, queryDTO.getKeyword())
                .or()
                .like(Movie::getActors, queryDTO.getKeyword())
                .orderByDesc(Movie::getScore);

        Page<Movie> page = movieMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        return toMovieListPageVO(page);
    }

    @Override
    public MovieDetailVO getMovieDetail(Long id, Long userId) {
        Movie movie = movieMapper.selectById(id);
        if (movie == null) {
            throw new BusinessException(ResultCode.MOVIE_NOT_FOUND);
        }

        MovieDetailVO vo = new MovieDetailVO();
        vo.setId(movie.getId());
        vo.setTitle(movie.getTitle());
        vo.setEnglishTitle(movie.getEnglishTitle());
        vo.setPoster(movie.getPoster());
        vo.setDirector(movie.getDirector());
        vo.setActors(movie.getActors());
        vo.setDuration(movie.getDuration());
        vo.setCategoryIds(movie.getCategoryIds());
        vo.setCategoryNames(movieCategoryService.getCategoryNames(movie.getCategoryIds()));
        vo.setRegion(movie.getRegion());
        vo.setLanguage(movie.getLanguage());
        vo.setReleaseDate(movie.getReleaseDate());
        vo.setOffDate(movie.getOffDate());
        vo.setSynopsis(movie.getSynopsis());
        vo.setTrailerUrl(movie.getTrailerUrl());
        vo.setScore(movie.getScore());
        vo.setScoreCount(movie.getScoreCount());
        vo.setStatus(movie.getStatus());

        // 判断是否收藏
        if (userId != null) {
            Long count = userFavoriteMapper.selectCount(
                    new LambdaQueryWrapper<UserFavorite>()
                            .eq(UserFavorite::getUserId, userId)
                            .eq(UserFavorite::getTargetType, 1) // 电影
                            .eq(UserFavorite::getTargetId, id)
            );
            vo.setIsFavorite(count > 0);
        } else {
            vo.setIsFavorite(false);
        }

        // 获取想看人数（从收藏数统计）
        Long wantCount = userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getTargetType, 1)
                        .eq(UserFavorite::getTargetId, id)
        );
        vo.setWantCount(wantCount.intValue());

        return vo;
    }

    @Override
    public List<MovieActorVO> getMovieActors(Long movieId) {
        List<MovieActor> actors = movieActorMapper.selectList(
                new LambdaQueryWrapper<MovieActor>()
                        .eq(MovieActor::getMovieId, movieId)
                        .orderByAsc(MovieActor::getSort)
        );
        return actors.stream().map(this::toActorVO).collect(Collectors.toList());
    }

    @Override
    public PageVO<ReviewVO> getMovieReviews(Long movieId, ReviewQueryDTO queryDTO, Long userId) {
        LambdaQueryWrapper<MovieReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MovieReview::getMovieId, movieId)
                .eq(MovieReview::getStatus, 1) // 显示状态
                .orderByDesc("like".equals(queryDTO.getOrderBy()) ? MovieReview::getLikeCount : MovieReview::getCreateTime);

        Page<MovieReview> page = movieReviewMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        List<ReviewVO> list = page.getRecords().stream()
                .map(review -> toReviewVO(review, userId))
                .collect(Collectors.toList());

        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createReview(Long userId, ReviewCreateDTO createDTO) {
        // 检查电影是否存在
        Movie movie = movieMapper.selectById(createDTO.getMovieId());
        if (movie == null) {
            throw new BusinessException(ResultCode.MOVIE_NOT_FOUND);
        }

        // 检查是否已评价
        Long count = movieReviewMapper.selectCount(
                new LambdaQueryWrapper<MovieReview>()
                        .eq(MovieReview::getUserId, userId)
                        .eq(MovieReview::getMovieId, createDTO.getMovieId())
        );
        if (count > 0) {
            throw new BusinessException("您已评价过该电影");
        }

        MovieReview review = new MovieReview();
        review.setUserId(userId);
        review.setMovieId(createDTO.getMovieId());
        review.setScore(createDTO.getScore());
        review.setContent(createDTO.getContent());
        review.setLikeCount(0);
        review.setStatus(1);
        movieReviewMapper.insert(review);

        // 更新电影评分
        updateMovieScore(createDTO.getMovieId());

        return review.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReview(Long userId, Long id) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN.getCode(), "无权删除他人影评");
        }

        movieReviewMapper.deleteById(id);

        // 更新电影评分
        updateMovieScore(review.getMovieId());
    }

    @Override
    public Integer likeReview(Long userId, Long id) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }

        // 使用数据库方式处理点赞（Redis可选）
        if (stringRedisTemplate != null) {
            String userKey = REVIEW_LIKE_USER_KEY + userId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(userKey, id.toString());
            if (Boolean.TRUE.equals(isMember)) {
                throw new BusinessException("您已点赞过该影评");
            }
            stringRedisTemplate.opsForSet().add(userKey, id.toString());
        }

        // 增加点赞数
        review.setLikeCount(review.getLikeCount() + 1);
        movieReviewMapper.updateById(review);

        return review.getLikeCount();
    }

    @Override
    public Integer unlikeReview(Long userId, Long id) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }

        // 使用数据库方式处理取消点赞（Redis可选）
        if (stringRedisTemplate != null) {
            String userKey = REVIEW_LIKE_USER_KEY + userId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(userKey, id.toString());
            if (!Boolean.TRUE.equals(isMember)) {
                throw new BusinessException("您未点赞过该影评");
            }
            stringRedisTemplate.opsForSet().remove(userKey, id.toString());
        }

        // 减少点赞数
        int likeCount = Math.max(0, review.getLikeCount() - 1);
        review.setLikeCount(likeCount);
        movieReviewMapper.updateById(review);

        return likeCount;
    }

    // ============== B端管理接口 ==============

    @Override
    public PageVO<MovieListVO> listMoviesAdmin(MovieQueryDTO queryDTO) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(Movie::getTitle, queryDTO.getKeyword());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Movie::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(Movie::getCreateTime);

        Page<Movie> page = movieMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        return toMovieListPageVO(page);
    }

    @Override
    public MovieDetailVO getMovieDetailAdmin(Long id) {
        return getMovieDetail(id, null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createMovie(MovieSaveDTO saveDTO) {
        Movie movie = new Movie();
        copyProperties(saveDTO, movie);
        movieMapper.insert(movie);
        return movie.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateMovie(Long id, MovieSaveDTO saveDTO) {
        Movie movie = movieMapper.selectById(id);
        if (movie == null) {
            throw new BusinessException(ResultCode.MOVIE_NOT_FOUND);
        }
        copyProperties(saveDTO, movie);
        movieMapper.updateById(movie);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMovie(Long id) {
        movieMapper.deleteById(id);
    }

    // ============== B端影评管理接口 ==============

    @Override
    public PageVO<AdminReviewListVO> listReviewsAdmin(AdminReviewQueryDTO queryDTO) {
        LambdaQueryWrapper<MovieReview> wrapper = new LambdaQueryWrapper<>();

        if (queryDTO.getMovieId() != null) {
            wrapper.eq(MovieReview::getMovieId, queryDTO.getMovieId());
        }
        if (queryDTO.getUserId() != null) {
            wrapper.eq(MovieReview::getUserId, queryDTO.getUserId());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(MovieReview::getStatus, queryDTO.getStatus());
        }
        wrapper.orderByDesc(MovieReview::getCreateTime);

        Page<MovieReview> page = movieReviewMapper.selectPage(
                new Page<>(queryDTO.getPage(), queryDTO.getPageSize()),
                wrapper
        );

        List<AdminReviewListVO> list = page.getRecords().stream()
                .map(this::toAdminReviewListVO)
                .collect(Collectors.toList());

        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public AdminReviewDetailVO getReviewDetailAdmin(Long id) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }
        return toAdminReviewDetailVO(review);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditReview(Long id, AuditReviewRequest request) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }

        review.setStatus(request.getStatus());
        movieReviewMapper.updateById(review);

        // 更新电影评分
        updateMovieScore(review.getMovieId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteReviewAdmin(Long id) {
        MovieReview review = movieReviewMapper.selectById(id);
        if (review == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "影评不存在");
        }

        movieReviewMapper.deleteById(id);

        // 更新电影评分
        updateMovieScore(review.getMovieId());
    }

    // ============== 私有方法 ==============

    private PageVO<MovieListVO> toMovieListPageVO(Page<Movie> page) {
        List<MovieListVO> list = page.getRecords().stream()
                .map(this::toMovieListVO)
                .collect(Collectors.toList());
        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    private MovieListVO toMovieListVO(Movie movie) {
        MovieListVO vo = new MovieListVO();
        vo.setId(movie.getId());
        vo.setTitle(movie.getTitle());
        vo.setPoster(movie.getPoster());
        vo.setScore(movie.getScore());
        vo.setDuration(movie.getDuration());
        vo.setCategoryIds(movie.getCategoryIds());
        vo.setCategoryNames(movieCategoryService.getCategoryNames(movie.getCategoryIds()));
        vo.setReleaseDate(movie.getReleaseDate());
        vo.setStatus(movie.getStatus());

        // 获取想看人数
        Long wantCount = userFavoriteMapper.selectCount(
                new LambdaQueryWrapper<UserFavorite>()
                        .eq(UserFavorite::getTargetType, 1)
                        .eq(UserFavorite::getTargetId, movie.getId())
        );
        vo.setWantCount(wantCount.intValue());

        return vo;
    }

    private MovieActorVO toActorVO(MovieActor actor) {
        MovieActorVO vo = new MovieActorVO();
        vo.setId(actor.getId());
        vo.setName(actor.getName());
        vo.setRole(actor.getRole());
        vo.setSort(actor.getSort());
        return vo;
    }

    private ReviewVO toReviewVO(MovieReview review, Long userId) {
        ReviewVO vo = new ReviewVO();
        vo.setId(review.getId());
        vo.setUserId(review.getUserId());
        vo.setMovieId(review.getMovieId());
        vo.setScore(review.getScore());
        vo.setContent(review.getContent());
        vo.setLikeCount(review.getLikeCount());
        vo.setCreateTime(review.getCreateTime());

        // 获取用户信息（暂时用模拟数据，后续对接用户模块）
        vo.setNickname("用户" + review.getUserId());
        vo.setAvatar("https://via.placeholder.com/100");

        // 判断是否点赞（Redis可选）
        if (userId != null && stringRedisTemplate != null) {
            String userKey = REVIEW_LIKE_USER_KEY + userId;
            Boolean isMember = stringRedisTemplate.opsForSet().isMember(userKey, review.getId().toString());
            vo.setIsLiked(Boolean.TRUE.equals(isMember));
        } else {
            vo.setIsLiked(false);
        }

        return vo;
    }

    private AdminReviewListVO toAdminReviewListVO(MovieReview review) {
        AdminReviewListVO vo = new AdminReviewListVO();
        vo.setId(review.getId());
        vo.setUserId(review.getUserId());
        vo.setMovieId(review.getMovieId());
        vo.setScore(review.getScore());
        vo.setContent(review.getContent());
        vo.setLikeCount(review.getLikeCount());
        vo.setStatus(review.getStatus());
        vo.setCreateTime(review.getCreateTime());

        // 获取用户信息
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
        } else {
            vo.setNickname("用户已删除");
            vo.setAvatar("");
        }

        // 获取电影信息
        Movie movie = movieMapper.selectById(review.getMovieId());
        if (movie != null) {
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
        } else {
            vo.setMovieTitle("电影已删除");
            vo.setMoviePoster("");
        }

        return vo;
    }

    private AdminReviewDetailVO toAdminReviewDetailVO(MovieReview review) {
        AdminReviewDetailVO vo = new AdminReviewDetailVO();
        vo.setId(review.getId());
        vo.setUserId(review.getUserId());
        vo.setMovieId(review.getMovieId());
        vo.setScore(review.getScore());
        vo.setContent(review.getContent());
        vo.setLikeCount(review.getLikeCount());
        vo.setStatus(review.getStatus());
        vo.setCreateTime(review.getCreateTime());
        vo.setUpdateTime(review.getUpdateTime());

        // 获取用户信息
        User user = userMapper.selectById(review.getUserId());
        if (user != null) {
            vo.setNickname(user.getNickname());
            vo.setAvatar(user.getAvatar());
            vo.setUserPhone(user.getPhone());
        } else {
            vo.setNickname("用户已删除");
            vo.setAvatar("");
            vo.setUserPhone("");
        }

        // 获取电影信息
        Movie movie = movieMapper.selectById(review.getMovieId());
        if (movie != null) {
            vo.setMovieTitle(movie.getTitle());
            vo.setMoviePoster(movie.getPoster());
        } else {
            vo.setMovieTitle("电影已删除");
            vo.setMoviePoster("");
        }

        return vo;
    }

    private void copyProperties(MovieSaveDTO dto, Movie movie) {
        movie.setTitle(dto.getTitle());
        movie.setEnglishTitle(dto.getEnglishTitle());
        movie.setPoster(dto.getPoster());
        movie.setDirector(dto.getDirector());
        movie.setActors(dto.getActors());
        movie.setDuration(dto.getDuration());
        movie.setCategoryIds(dto.getCategoryIds());
        movie.setRegion(dto.getRegion());
        movie.setLanguage(dto.getLanguage());
        movie.setReleaseDate(dto.getReleaseDate());
        movie.setOffDate(dto.getOffDate());
        movie.setSynopsis(dto.getSynopsis());
        movie.setTrailerUrl(dto.getTrailerUrl());
        if (dto.getScore() != null) {
            movie.setScore(java.math.BigDecimal.valueOf(dto.getScore()));
        }
        movie.setStatus(dto.getStatus());
        movie.setSort(dto.getSort() != null ? dto.getSort() : 0);
    }

    /**
     * 更新电影评分
     */
    private void updateMovieScore(Long movieId) {
        List<MovieReview> reviews = movieReviewMapper.selectList(
                new LambdaQueryWrapper<MovieReview>()
                        .eq(MovieReview::getMovieId, movieId)
                        .eq(MovieReview::getStatus, 1)
        );

        if (reviews.isEmpty()) {
            return;
        }

        double avgScore = reviews.stream()
                .mapToInt(MovieReview::getScore)
                .average()
                .orElse(0.0);

        Movie movie = new Movie();
        movie.setId(movieId);
        movie.setScore(java.math.BigDecimal.valueOf(avgScore).setScale(1, java.math.RoundingMode.HALF_UP));
        movie.setScoreCount(reviews.size());
        movieMapper.updateById(movie);
    }

}
