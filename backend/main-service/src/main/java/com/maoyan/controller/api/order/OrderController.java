package com.maoyan.controller.api.order;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.order.*;
import com.maoyan.service.OrderService;
import com.maoyan.vo.order.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端订单控制器
 *
 * @author maoyan
 */
@Tag(name = "C端-订单接口", description = "选座购票、支付、退票相关接口")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /**
     * 创建订单（电影票）
     */
    @Operation(summary = "创建订单", description = "创建电影票订单")
    @PostMapping
    public Result<OrderCreateVO> createOrder(
            @Valid @RequestBody OrderCreateDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.createOrder(dto, userId));
    }

    /**
     * 创建组合订单（电影票+卖品）
     */
    @Operation(summary = "创建组合订单", description = "创建电影票+卖品组合订单")
    @PostMapping("/combo")
    public Result<OrderCreateVO> createComboOrder(
            @Valid @RequestBody ComboOrderCreateDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.createComboOrder(dto, userId));
    }

    /**
     * 获取支付信息
     */
    @Operation(summary = "获取支付信息", description = "获取订单支付信息")
    @GetMapping("/{id}/pay-info")
    public Result<PayInfoVO> getPayInfo(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.getPayInfo(id, userId));
    }

    /**
     * 取消订单
     */
    @Operation(summary = "取消订单", description = "取消待支付订单")
    @PostMapping("/{id}/cancel")
    public Result<Void> cancelOrder(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        orderService.cancelOrder(id, userId);
        return Result.success();
    }

    /**
     * 获取订单列表
     */
    @Operation(summary = "获取订单列表", description = "获取当前用户的订单列表")
    @GetMapping
    public Result<Page<OrderListItemVO>> getOrderList(
            @Parameter(description = "订单状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "订单类型") @RequestParam(required = false) Integer type,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        OrderQueryDTO dto = new OrderQueryDTO();
        dto.setStatus(status);
        dto.setType(type);
        dto.setPage(page);
        dto.setPageSize(pageSize);
        return Result.success(orderService.getOrderList(dto, userId));
    }

    /**
     * 获取订单详情
     */
    @Operation(summary = "获取订单详情", description = "获取订单详细信息")
    @GetMapping("/{id}")
    public Result<OrderDetailVO> getOrderDetail(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.getOrderDetail(id, userId));
    }

    /**
     * 获取取票码
     */
    @Operation(summary = "获取取票码", description = "获取电影票取票码")
    @GetMapping("/{id}/pick-code")
    public Result<PickCodeVO> getPickCode(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.getPickCode(id, userId));
    }

    /**
     * 检查退票条件
     */
    @Operation(summary = "检查退票条件", description = "检查订单是否可以退票")
    @GetMapping("/{id}/refund-check")
    public Result<RefundCheckVO> checkRefund(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.checkRefund(id, userId));
    }

    /**
     * 申请退票
     */
    @Operation(summary = "申请退票", description = "申请订单退票")
    @PostMapping("/{id}/refund")
    public Result<RefundApplyVO> applyRefund(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @Valid @RequestBody RefundApplyDTO dto,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.applyRefund(id, dto, userId));
    }

    /**
     * 获取卖品取货码
     */
    @Operation(summary = "获取卖品取货码", description = "获取卖品取货码")
    @GetMapping("/{id}/snack-pick-code")
    public Result<SnackPickCodeVO> getSnackPickCode(
            @Parameter(description = "订单ID") @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(orderService.getSnackPickCode(id, userId));
    }

}
