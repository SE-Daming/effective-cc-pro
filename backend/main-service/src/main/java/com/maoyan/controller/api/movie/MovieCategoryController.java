package com.maoyan.controller.api.movie;

import com.maoyan.common.Result;
import com.maoyan.service.MovieCategoryService;
import com.maoyan.vo.movie.MovieCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * C端 - 电影分类接口
 *
 * @author maoyan
 */
@Tag(name = "C端-电影分类接口", description = "电影分类列表接口")
@RestController
@RequestMapping("/api/movie-categories")
@RequiredArgsConstructor
public class MovieCategoryController {

    private final MovieCategoryService movieCategoryService;

    @Operation(summary = "获取电影分类列表")
    @GetMapping
    public Result<List<MovieCategoryVO>> getAllCategories() {
        return Result.success(movieCategoryService.getAllCategories());
    }

}
