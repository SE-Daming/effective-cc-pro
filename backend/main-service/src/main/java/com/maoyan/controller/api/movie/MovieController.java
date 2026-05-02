package com.maoyan.controller.api.movie;

import com.maoyan.common.Result;
import com.maoyan.dto.movie.MovieQueryDTO;
import com.maoyan.dto.movie.ReviewQueryDTO;
import com.maoyan.service.MovieService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.movie.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端 - 电影接口
 *
 * @author maoyan
 */
@Tag(name = "C端-电影接口", description = "电影列表、详情、影评等接口")
@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @Operation(summary = "正在热映")
    @GetMapping("/now-playing")
    public Result<PageVO<MovieListVO>> getNowPlaying(@Valid MovieQueryDTO queryDTO) {
        return Result.success(movieService.getNowPlaying(queryDTO));
    }

    @Operation(summary = "即将上映")
    @GetMapping("/coming-soon")
    public Result<PageVO<MovieListVO>> getComingSoon(@Valid MovieQueryDTO queryDTO) {
        return Result.success(movieService.getComingSoon(queryDTO));
    }

    @Operation(summary = "电影搜索")
    @GetMapping("/search")
    public Result<PageVO<MovieListVO>> searchMovies(@Valid MovieQueryDTO queryDTO) {
        return Result.success(movieService.searchMovies(queryDTO));
    }

    @Operation(summary = "电影详情")
    @GetMapping("/{id}")
    public Result<MovieDetailVO> getMovieDetail(
            @Parameter(description = "电影ID") @PathVariable Long id,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        return Result.success(movieService.getMovieDetail(id, userId));
    }

    @Operation(summary = "电影演员列表")
    @GetMapping("/{id}/actors")
    public Result<List<MovieActorVO>> getMovieActors(
            @Parameter(description = "电影ID") @PathVariable Long id) {
        return Result.success(movieService.getMovieActors(id));
    }

    @Operation(summary = "电影影评列表")
    @GetMapping("/{id}/reviews")
    public Result<PageVO<ReviewVO>> getMovieReviews(
            @Parameter(description = "电影ID") @PathVariable Long id,
            @Valid ReviewQueryDTO queryDTO,
            @Parameter(description = "用户ID（可选）") @RequestParam(required = false) Long userId) {
        return Result.success(movieService.getMovieReviews(id, queryDTO, userId));
    }

}
