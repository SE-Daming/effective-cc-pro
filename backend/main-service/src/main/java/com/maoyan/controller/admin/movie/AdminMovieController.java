package com.maoyan.controller.admin.movie;

import com.maoyan.common.Result;
import com.maoyan.dto.movie.MovieQueryDTO;
import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.service.MovieService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.movie.MovieDetailVO;
import com.maoyan.vo.movie.MovieListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * B端 - 电影管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-电影管理", description = "电影CRUD接口")
@RestController
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class AdminMovieController {

    private final MovieService movieService;

    @Operation(summary = "获取电影列表")
    @GetMapping
    public Result<PageVO<MovieListVO>> listMovies(@Valid MovieQueryDTO queryDTO) {
        return Result.success(movieService.listMoviesAdmin(queryDTO));
    }

    @Operation(summary = "获取电影详情")
    @GetMapping("/{id}")
    public Result<MovieDetailVO> getMovieDetail(
            @Parameter(description = "电影ID") @PathVariable Long id) {
        return Result.success(movieService.getMovieDetailAdmin(id));
    }

    @Operation(summary = "新增电影")
    @PostMapping
    public Result<Long> createMovie(@Valid @RequestBody MovieSaveDTO saveDTO) {
        return Result.success(movieService.createMovie(saveDTO));
    }

    @Operation(summary = "更新电影")
    @PutMapping("/{id}")
    public Result<Void> updateMovie(
            @Parameter(description = "电影ID") @PathVariable Long id,
            @Valid @RequestBody MovieSaveDTO saveDTO) {
        movieService.updateMovie(id, saveDTO);
        return Result.success();
    }

    @Operation(summary = "删除电影")
    @DeleteMapping("/{id}")
    public Result<Void> deleteMovie(
            @Parameter(description = "电影ID") @PathVariable Long id) {
        movieService.deleteMovie(id);
        return Result.success();
    }

}
