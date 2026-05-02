package com.maoyan.service;

import com.maoyan.dto.coupon.*;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.coupon.*;

/**
 * 优惠券服务接口
 *
 * @author maoyan
 */
public interface CouponService {

    /**
     * 获取可领取优惠券列表
     *
     * @param userId 用户ID
     * @param query 查询参数
     * @return 优惠券列表
     */
    PageVO<AvailableCouponVO> getAvailableCoupons(Long userId, AvailableCouponQueryDTO query);

    /**
     * 领取优惠券
     *
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 领取结果
     */
    ReceiveCouponVO receiveCoupon(Long userId, Long couponId);

    /**
     * 获取优惠券详情
     *
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    CouponDetailVO getCouponDetail(Long couponId);

    /**
     * 获取用户优惠券列表
     *
     * @param userId 用户ID
     * @param query 查询参数
     * @return 用户优惠券列表
     */
    PageVO<UserCouponVO> getUserCoupons(Long userId, UserCouponQueryDTO query);

    /**
     * 获取可用优惠券（下单时）
     *
     * @param userId 用户ID
     * @param query 查询参数
     * @return 可用优惠券列表
     */
    UsableCouponListVO getUsableCoupons(Long userId, UsableCouponQueryDTO query);

    // ==================== B端管理接口 ====================

    /**
     * 获取优惠券列表（管理端）
     *
     * @param query 查询参数
     * @return 优惠券列表
     */
    PageVO<AdminCouponVO> getAdminCouponList(AdminCouponQueryDTO query);

    /**
     * 创建优惠券
     *
     * @param dto 优惠券参数
     * @return 优惠券ID
     */
    Long createCoupon(CouponSaveDTO dto);

    /**
     * 更新优惠券
     *
     * @param couponId 优惠券ID
     * @param dto 优惠券参数
     */
    void updateCoupon(Long couponId, CouponSaveDTO dto);

    /**
     * 删除优惠券
     *
     * @param couponId 优惠券ID
     */
    void deleteCoupon(Long couponId);

    /**
     * 发放优惠券
     *
     * @param couponId 优惠券ID
     * @param dto 发放参数
     * @return 发放数量
     */
    Integer distributeCoupon(Long couponId, CouponDistributeDTO dto);

}
