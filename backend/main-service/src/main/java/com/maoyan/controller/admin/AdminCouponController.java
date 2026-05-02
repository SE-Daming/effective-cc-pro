package com.maoyan.controller.admin;

import com.maoyan.common.Result;
import com.maoyan.dto.coupon.AdminCouponQueryDTO;
import com.maoyan.dto.coupon.CouponDistributeDTO;
import com.maoyan.dto.coupon.CouponSaveDTO;
import com.maoyan.service.CouponService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.coupon.AdminCouponVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * B端优惠券管理控制器
 *
 * @author maoyan
 */
@Tag(name = "优惠券管理", description = "优惠券CRUD、发放等管理接口")
@RestController
@RequestMapping("/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    /**
     * 获取优惠券列表
     */
    @Operation(summary = "获取优惠券列表")
    @GetMapping
    public Result<PageVO<AdminCouponVO>> getCouponList(@Valid AdminCouponQueryDTO query) {
        PageVO<AdminCouponVO> result = couponService.getAdminCouponList(query);
        return Result.success(result);
    }

    /**
     * 新增优惠券
     */
    @Operation(summary = "新增优惠券")
    @PostMapping
    public Result<Long> createCoupon(@Valid @RequestBody CouponSaveDTO dto) {
        Long couponId = couponService.createCoupon(dto);
        return Result.success(couponId);
    }

    /**
     * 更新优惠券
     */
    @Operation(summary = "更新优惠券")
    @PutMapping("/{id}")
    public Result<Void> updateCoupon(
            @Parameter(description = "优惠券ID") @PathVariable Long id,
            @Valid @RequestBody CouponSaveDTO dto) {
        couponService.updateCoupon(id, dto);
        return Result.success();
    }

    /**
     * 删除优惠券
     */
    @Operation(summary = "删除优惠券")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCoupon(
            @Parameter(description = "优惠券ID") @PathVariable Long id) {
        couponService.deleteCoupon(id);
        return Result.success();
    }

    /**
     * 发放优惠券
     */
    @Operation(summary = "发放优惠券")
    @PostMapping("/{id}/distribute")
    public Result<Integer> distributeCoupon(
            @Parameter(description = "优惠券ID") @PathVariable Long id,
            @RequestBody CouponDistributeDTO dto) {
        Integer count = couponService.distributeCoupon(id, dto);
        return Result.success(count);
    }

}
