package com.maoyan.controller.api.snack;

import com.maoyan.common.Result;
import com.maoyan.dto.snack.SnackOrderCreateDTO;
import com.maoyan.dto.snack.SnackQueryDTO;
import com.maoyan.service.SnackService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.snack.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * C端 - 卖品接口
 *
 * @author maoyan
 */
@Tag(name = "C端-卖品接口", description = "卖品分类、列表、购买等接口")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SnackController {

    private final SnackService snackService;

    @Operation(summary = "获取卖品分类列表")
    @GetMapping("/snack-categories")
    public Result<List<SnackCategoryVO>> getCategories() {
        return Result.success(snackService.getCategories());
    }

    @Operation(summary = "获取卖品列表")
    @GetMapping("/snacks")
    public Result<PageVO<SnackListVO>> getSnackList(@Valid SnackQueryDTO queryDTO) {
        return Result.success(snackService.getSnackList(queryDTO));
    }

    @Operation(summary = "获取卖品详情")
    @GetMapping("/snacks/{id}")
    public Result<SnackDetailVO> getSnackDetail(
            @Parameter(description = "卖品ID") @PathVariable Long id) {
        return Result.success(snackService.getSnackDetail(id));
    }

    @Operation(summary = "创建卖品订单")
    @PostMapping("/snack-orders")
    public Result<SnackOrderCreateVO> createSnackOrder(
            @Valid @RequestBody SnackOrderCreateDTO dto,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return Result.success(snackService.createSnackOrder(dto, userId));
    }

    @Operation(summary = "获取卖品订单详情")
    @GetMapping("/snack-orders/{id}")
    public Result<SnackOrderDetailVO> getSnackOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Parameter(description = "用户ID") @RequestParam Long userId) {
        return Result.success(snackService.getSnackOrderDetail(id, userId));
    }

    @Operation(summary = "获取卖品订单列表")
    @GetMapping("/snack-orders")
    public Result<PageVO<SnackOrderListVO>> getSnackOrderList(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(snackService.getSnackOrderList(status, userId, page, pageSize));
    }
}
