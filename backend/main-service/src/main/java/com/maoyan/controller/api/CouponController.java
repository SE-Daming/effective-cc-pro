package com.maoyan.controller.api;

import com.maoyan.common.Result;
import com.maoyan.dto.coupon.AvailableCouponQueryDTO;
import com.maoyan.dto.coupon.UsableCouponQueryDTO;
import com.maoyan.dto.coupon.UserCouponQueryDTO;
import com.maoyan.service.CouponService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.coupon.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端优惠券控制器
 *
 * @author maoyan
 */
@Tag(name = "优惠券", description = "优惠券领取、查询相关接口")
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    /**
     * 获取可领取优惠券列表
     */
    @Operation(summary = "获取可领取优惠券列表")
    @GetMapping("/available")
    public Result<PageVO<AvailableCouponVO>> getAvailableCoupons(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid AvailableCouponQueryDTO query) {
        PageVO<AvailableCouponVO> result = couponService.getAvailableCoupons(userId, query);
        return Result.success(result);
    }

    /**
     * 领取优惠券
     */
    @Operation(summary = "领取优惠券")
    @PostMapping("/{id}/receive")
    public Result<ReceiveCouponVO> receiveCoupon(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Parameter(description = "优惠券ID") @PathVariable Long id) {
        ReceiveCouponVO result = couponService.receiveCoupon(userId, id);
        return Result.success(result);
    }

    /**
     * 获取优惠券详情
     */
    @Operation(summary = "获取优惠券详情")
    @GetMapping("/{id}")
    public Result<CouponDetailVO> getCouponDetail(
            @Parameter(description = "优惠券ID") @PathVariable Long id) {
        CouponDetailVO result = couponService.getCouponDetail(id);
        return Result.success(result);
    }

    /**
     * 获取用户优惠券列表
     */
    @Operation(summary = "获取用户优惠券列表")
    @GetMapping("/user-coupons")
    public Result<PageVO<UserCouponVO>> getUserCoupons(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid UserCouponQueryDTO query) {
        PageVO<UserCouponVO> result = couponService.getUserCoupons(userId, query);
        return Result.success(result);
    }

    /**
     * 获取可用优惠券（下单时）
     */
    @Operation(summary = "获取可用优惠券（下单时）")
    @GetMapping("/usable")
    public Result<UsableCouponListVO> getUsableCoupons(
            @Parameter(description = "用户ID") @RequestParam Long userId,
            @Valid UsableCouponQueryDTO query) {
        UsableCouponListVO result = couponService.getUsableCoupons(userId, query);
        return Result.success(result);
    }

}
