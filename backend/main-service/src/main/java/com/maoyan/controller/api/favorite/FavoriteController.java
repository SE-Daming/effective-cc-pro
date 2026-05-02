package com.maoyan.controller.api.favorite;

import com.maoyan.common.Result;
import com.maoyan.dto.favorite.FavoriteDTO;
import com.maoyan.service.FavoriteService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.favorite.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端 - 收藏接口
 *
 * @author maoyan
 */
@Tag(name = "C端-收藏接口", description = "收藏电影、影院等接口")
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "添加收藏")
    @PostMapping
    public Result<FavoriteVO> addFavorite(
            @Valid @RequestBody FavoriteDTO dto,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return Result.success(favoriteService.addFavorite(dto, userId));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping
    public Result<Void> removeFavorite(
            @Valid @RequestBody FavoriteDTO dto,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        favoriteService.removeFavorite(dto, userId);
        return Result.success();
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/check")
    public Result<FavoriteCheckVO> checkFavorite(
            @Parameter(description = "类型：1电影 2影院") @RequestParam Integer targetType,
            @Parameter(description = "目标ID") @RequestParam Long targetId,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return Result.success(favoriteService.checkFavorite(targetType, targetId, userId));
    }

    @Operation(summary = "获取收藏的电影列表")
    @GetMapping("/movies")
    public Result<PageVO<FavoriteMovieVO>> getFavoriteMovies(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(favoriteService.getFavoriteMovies(userId, page, pageSize));
    }

    @Operation(summary = "获取收藏的影院列表")
    @GetMapping("/cinemas")
    public Result<PageVO<FavoriteCinemaVO>> getFavoriteCinemas(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(favoriteService.getFavoriteCinemas(userId, page, pageSize));
    }

    @Operation(summary = "获取收藏数量统计")
    @GetMapping("/count")
    public Result<FavoriteCountVO> getFavoriteCount(
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return Result.success(favoriteService.getFavoriteCount(userId));
    }
}
