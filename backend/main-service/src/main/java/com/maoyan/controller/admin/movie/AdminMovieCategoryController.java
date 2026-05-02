package com.maoyan.controller.admin.movie;

import com.maoyan.common.Result;
import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.service.MovieCategoryService;
import com.maoyan.vo.movie.MovieCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * B端 - 电影分类管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-电影分类管理", description = "电影分类CRUD接口")
@RestController
@RequestMapping("/api/admin/movie-categories")
@RequiredArgsConstructor
public class AdminMovieCategoryController {

    private final MovieCategoryService movieCategoryService;

    @Operation(summary = "获取分类列表")
    @GetMapping
    public Result<List<MovieCategoryVO>> listCategories() {
        return Result.success(movieCategoryService.getAllCategories());
    }

    @Operation(summary = "获取分类详情")
    @GetMapping("/{id}")
    public Result<MovieCategoryVO> getCategory(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        return Result.success(movieCategoryService.getCategoryById(id));
    }

    @Operation(summary = "新增分类")
    @PostMapping
    public Result<Integer> createCategory(@Valid @RequestBody MovieSaveDTO saveDTO) {
        return Result.success(movieCategoryService.createCategory(saveDTO));
    }

    @Operation(summary = "更新分类")
    @PutMapping("/{id}")
    public Result<Void> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Integer id,
            @Valid @RequestBody MovieSaveDTO saveDTO) {
        movieCategoryService.updateCategory(id, saveDTO);
        return Result.success();
    }

    @Operation(summary = "删除分类")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Integer id) {
        movieCategoryService.deleteCategory(id);
        return Result.success();
    }

}
