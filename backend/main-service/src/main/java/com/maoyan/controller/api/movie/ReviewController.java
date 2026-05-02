package com.maoyan.controller.api.movie;

import com.maoyan.common.Result;
import com.maoyan.dto.movie.ReviewCreateDTO;
import com.maoyan.service.MovieService;
import com.maoyan.vo.movie.ReviewVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端 - 影评接口
 *
 * @author maoyan
 */
@Tag(name = "C端-影评接口", description = "影评发布、删除、点赞等接口")
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final MovieService movieService;

    // TODO: 后续对接用户认证模块，从请求头获取用户ID
    private static final Long MOCK_USER_ID = 1L;

    @Operation(summary = "发布影评")
    @PostMapping
    public Result<ReviewVO> createReview(@Valid @RequestBody ReviewCreateDTO createDTO) {
        Long reviewId = movieService.createReview(MOCK_USER_ID, createDTO);
        ReviewVO vo = new ReviewVO();
        vo.setId(reviewId);
        vo.setMovieId(createDTO.getMovieId());
        vo.setScore(createDTO.getScore());
        vo.setContent(createDTO.getContent());
        return Result.success(vo);
    }

    @Operation(summary = "删除影评")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(
            @Parameter(description = "影评ID") @PathVariable Long id) {
        movieService.deleteReview(MOCK_USER_ID, id);
        return Result.success();
    }

    @Operation(summary = "点赞影评")
    @PostMapping("/{id}/like")
    public Result<Integer> likeReview(
            @Parameter(description = "影评ID") @PathVariable Long id) {
        Integer likeCount = movieService.likeReview(MOCK_USER_ID, id);
        return Result.success(likeCount);
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/{id}/like")
    public Result<Integer> unlikeReview(
            @Parameter(description = "影评ID") @PathVariable Long id) {
        Integer likeCount = movieService.unlikeReview(MOCK_USER_ID, id);
        return Result.success(likeCount);
    }

}
