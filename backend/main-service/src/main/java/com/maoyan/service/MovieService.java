package com.maoyan.service;

import com.maoyan.dto.movie.MovieQueryDTO;
import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.dto.movie.ReviewCreateDTO;
import com.maoyan.dto.movie.ReviewQueryDTO;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.movie.*;

import java.util.List;

/**
 * 电影服务接口
 *
 * @author maoyan
 */
public interface MovieService {

    /**
     * 获取正在热映电影列表
     *
     * @param queryDTO 查询参数
     * @return 电影列表
     */
    PageVO<MovieListVO> getNowPlaying(MovieQueryDTO queryDTO);

    /**
     * 获取即将上映电影列表
     *
     * @param queryDTO 查询参数
     * @return 电影列表
     */
    PageVO<MovieListVO> getComingSoon(MovieQueryDTO queryDTO);

    /**
     * 搜索电影
     *
     * @param queryDTO 查询参数
     * @return 电影列表
     */
    PageVO<MovieListVO> searchMovies(MovieQueryDTO queryDTO);

    /**
     * 获取电影详情
     *
     * @param id     电影ID
     * @param userId 用户ID（可选，用于判断是否收藏）
     * @return 电影详情
     */
    MovieDetailVO getMovieDetail(Long id, Long userId);

    /**
     * 获取电影演员列表
     *
     * @param movieId 电影ID
     * @return 演员列表
     */
    List<MovieActorVO> getMovieActors(Long movieId);

    /**
     * 获取电影影评列表
     *
     * @param movieId  电影ID
     * @param queryDTO 查询参数
     * @param userId   用户ID（可选，用于判断是否点赞）
     * @return 影评列表
     */
    PageVO<ReviewVO> getMovieReviews(Long movieId, ReviewQueryDTO queryDTO, Long userId);

    /**
     * 发布影评
     *
     * @param userId    用户ID
     * @param createDTO 影评参数
     * @return 影评ID
     */
    Long createReview(Long userId, ReviewCreateDTO createDTO);

    /**
     * 删除影评
     *
     * @param userId 用户ID
     * @param id     影评ID
     */
    void deleteReview(Long userId, Long id);

    /**
     * 点赞影评
     *
     * @param userId 用户ID
     * @param id     影评ID
     * @return 点赞数
     */
    Integer likeReview(Long userId, Long id);

    /**
     * 取消点赞
     *
     * @param userId 用户ID
     * @param id     影评ID
     * @return 点赞数
     */
    Integer unlikeReview(Long userId, Long id);

    // ============== B端管理接口 ==============

    /**
     * 分页查询电影（管理端）
     *
     * @param queryDTO 查询参数
     * @return 电影列表
     */
    PageVO<MovieListVO> listMoviesAdmin(MovieQueryDTO queryDTO);

    /**
     * 新增电影
     *
     * @param saveDTO 电影参数
     * @return 电影ID
     */
    Long createMovie(MovieSaveDTO saveDTO);

    /**
     * 更新电影
     *
     * @param id      电影ID
     * @param saveDTO 电影参数
     */
    void updateMovie(Long id, MovieSaveDTO saveDTO);

    /**
     * 删除电影
     *
     * @param id 电影ID
     */
    void deleteMovie(Long id);

}
