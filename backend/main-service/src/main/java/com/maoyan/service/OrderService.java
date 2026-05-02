package com.maoyan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.order.*;
import com.maoyan.vo.order.*;

/**
 * 订单服务接口
 *
 * @author maoyan
 */
public interface OrderService {

    /**
     * 创建订单（电影票）
     *
     * @param dto    创建请求
     * @param userId 用户ID
     * @return 订单创建结果
     */
    OrderCreateVO createOrder(OrderCreateDTO dto, Long userId);

    /**
     * 创建组合订单（电影票+卖品）
     *
     * @param dto    创建请求
     * @param userId 用户ID
     * @return 订单创建结果
     */
    OrderCreateVO createComboOrder(ComboOrderCreateDTO dto, Long userId);

    /**
     * 获取支付信息
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 支付信息
     */
    PayInfoVO getPayInfo(Long orderId, Long userId);

    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     */
    void cancelOrder(Long orderId, Long userId);

    /**
     * 获取订单列表（C端）
     *
     * @param dto    查询条件
     * @param userId 用户ID
     * @return 订单列表
     */
    Page<OrderListItemVO> getOrderList(OrderQueryDTO dto, Long userId);

    /**
     * 获取订单详情（C端）
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 订单详情
     */
    OrderDetailVO getOrderDetail(Long orderId, Long userId);

    /**
     * 获取取票码
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 取票码信息
     */
    PickCodeVO getPickCode(Long orderId, Long userId);

    /**
     * 检查退票条件
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 退票检查结果
     */
    RefundCheckVO checkRefund(Long orderId, Long userId);

    /**
     * 申请退票
     *
     * @param orderId 订单ID
     * @param dto     退票请求
     * @param userId  用户ID
     * @return 退票申请结果
     */
    RefundApplyVO applyRefund(Long orderId, RefundApplyDTO dto, Long userId);

    /**
     * 获取卖品取货码
     *
     * @param orderId 订单ID
     * @param userId  用户ID
     * @return 卖品取货码信息
     */
    SnackPickCodeVO getSnackPickCode(Long orderId, Long userId);

    /**
     * 获取订单列表（B端）
     *
     * @param dto 查询条件
     * @return 订单列表
     */
    Page<OrderListItemVO> getAdminOrderList(AdminOrderQueryDTO dto);

    /**
     * 获取订单详情（B端）
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    OrderDetailVO getAdminOrderDetail(Long orderId);

    /**
     * 退款审核
     *
     * @param orderId 订单ID
     * @param dto     审核请求
     * @param adminId 管理员ID
     * @return 审核结果
     */
    RefundAuditVO auditRefund(Long orderId, RefundAuditDTO dto, Integer adminId);

}
