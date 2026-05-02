package com.maoyan.controller.admin.review;

import com.maoyan.common.Result;
import com.maoyan.dto.movie.AdminReviewQueryDTO;
import com.maoyan.dto.movie.AuditReviewRequest;
import com.maoyan.service.MovieService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.movie.AdminReviewDetailVO;
import com.maoyan.vo.movie.AdminReviewListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * B端影评管理控制器
 *
 * @author maoyan
 */
@Tag(name = "B端-影评管理", description = "影评管理接口")
@RestController
@RequestMapping("/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final MovieService movieService;

    @Operation(summary = "影评列表")
    @GetMapping
    public Result<Map<String, Object>> getReviewList(AdminReviewQueryDTO queryDTO) {
        PageVO<AdminReviewListVO> page = movieService.listReviewsAdmin(queryDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getList());
        data.put("total", page.getTotal());
        data.put("page", page.getPage());
        data.put("pageSize", page.getPageSize());
        return Result.success(data);
    }

    @Operation(summary = "影评详情")
    @GetMapping("/{id}")
    public Result<AdminReviewDetailVO> getReviewDetail(
            @Parameter(description = "影评ID") @PathVariable Long id) {
        AdminReviewDetailVO vo = movieService.getReviewDetailAdmin(id);
        return Result.success(vo);
    }

    @Operation(summary = "审核影评")
    @PatchMapping("/{id}/audit")
    public Result<Void> auditReview(
            @Parameter(description = "影评ID") @PathVariable Long id,
            @Valid @RequestBody AuditReviewRequest request) {
        movieService.auditReview(id, request);
        return Result.success();
    }

    @Operation(summary = "删除影评")
    @DeleteMapping("/{id}")
    public Result<Void> deleteReview(
            @Parameter(description = "影评ID") @PathVariable Long id) {
        movieService.deleteReviewAdmin(id);
        return Result.success();
    }

}
