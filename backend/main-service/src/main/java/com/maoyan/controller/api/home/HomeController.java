package com.maoyan.controller.api.home;

import com.maoyan.common.Result;
import com.maoyan.service.HomeService;
import com.maoyan.vo.home.BannerVO;
import com.maoyan.vo.home.HotMovieVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端 - 首页接口
 *
 * @author maoyan
 */
@Tag(name = "C端-首页接口", description = "Banner、热门推荐等接口")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @Operation(summary = "获取Banner列表")
    @GetMapping("/banners")
    public Result<List<BannerVO>> getBanners() {
        return Result.success(homeService.getBanners());
    }

    @Operation(summary = "获取热门电影推荐")
    @GetMapping("/home/hot-movies")
    public Result<List<HotMovieVO>> getHotMovies(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(homeService.getHotMovies(limit));
    }
}
