package com.maoyan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.enums.OrderStatus;
import com.maoyan.common.enums.OrderType;
import com.maoyan.common.enums.SeatLockStatus;
import com.maoyan.dto.order.*;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.OrderService;
import com.maoyan.vo.order.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderTicketMapper orderTicketMapper;
    private final OrderSnackMapper orderSnackMapper;
    private final SeatLockMapper seatLockMapper;
    private final ScheduleMapper scheduleMapper;
    private final HallMapper hallMapper;
    private final CinemaMapper cinemaMapper;
    private final MovieMapper movieMapper;
    private final SnackMapper snackMapper;
    private final UserCouponMapper userCouponMapper;

    /**
     * 座位锁定时长（分钟）
     */
    private static final int LOCK_DURATION_MINUTES = 5;

    /**
     * 退票手续费率
     */
    private static final BigDecimal REFUND_FEE_RATE = new BigDecimal("0.10");

    /**
     * 退票限制时间（开场前小时数）
     */
    private static final int REFUND_LIMIT_HOURS = 2;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO createOrder(OrderCreateDTO dto, Long userId) {
        // 解析锁定ID
        String lockId = dto.getLockId();
        List<Long> seatIds = parseLockId(lockId);

        // 查询座位锁定记录
        LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
        lockWrapper.in(SeatLock::getSeatId, seatIds)
                .eq(SeatLock::getUserId, userId)
                .eq(SeatLock::getStatus, SeatLockStatus.LOCKED.getCode())
                .gt(SeatLock::getExpireTime, LocalDateTime.now());

        List<SeatLock> seatLocks = seatLockMapper.selectList(lockWrapper);
        if (seatLocks.isEmpty()) {
            throw new BusinessException("座位锁定已过期或不存在");
        }

        // 获取场次信息
        Long scheduleId = seatLocks.get(0).getScheduleId();
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }

        // 计算票价总额
        BigDecimal totalAmount = schedule.getPrice().multiply(new BigDecimal(seatLocks.size()));
        BigDecimal discountAmount = BigDecimal.ZERO;
        UserCoupon userCoupon = null;

        // 处理优惠券
        if (dto.getUserCouponId() != null) {
            userCoupon = validateAndUseCoupon(dto.getUserCouponId(), userId, totalAmount);
            discountAmount = calculateDiscount(userCoupon, totalAmount);
        }

        // 计算实付金额
        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setType(OrderType.TICKET.getCode());
        order.setCinemaId(schedule.getCinemaId());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setUserCouponId(dto.getUserCouponId());
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setExpireTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        orderMapper.insert(order);

        // 创建票务明细
        for (SeatLock seatLock : seatLocks) {
            OrderTicket orderTicket = new OrderTicket();
            orderTicket.setOrderId(order.getId());
            orderTicket.setScheduleId(scheduleId);
            orderTicket.setSeatId(seatLock.getSeatId());
            orderTicket.setSeatNo(seatLock.getSeatNo());
            orderTicket.setTicketPrice(schedule.getPrice());
            orderTicket.setStatus(1); // 未取票
            orderTicketMapper.insert(orderTicket);

            // 更新座位锁定记录，关联订单
            seatLock.setOrderId(order.getId());
            seatLockMapper.updateById(seatLock);
        }

        // 更新优惠券状态
        if (userCoupon != null) {
            userCoupon.setStatus(2); // 已使用
            userCoupon.setUseTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            userCouponMapper.updateById(userCoupon);
        }

        // 构建返回结果
        OrderCreateVO vo = new OrderCreateVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(totalAmount);
        vo.setDiscountAmount(discountAmount);
        vo.setPayAmount(payAmount);
        vo.setExpireTime(order.getExpireTime());
        vo.setPayInfo(buildMockPayInfo(order));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO createComboOrder(ComboOrderCreateDTO dto, Long userId) {
        // 解析锁定ID
        String lockId = dto.getLockId();
        List<Long> seatIds = parseLockId(lockId);

        // 查询座位锁定记录
        LambdaQueryWrapper<SeatLock> lockWrapper = new LambdaQueryWrapper<>();
        lockWrapper.in(SeatLock::getSeatId, seatIds)
                .eq(SeatLock::getUserId, userId)
                .eq(SeatLock::getStatus, SeatLockStatus.LOCKED.getCode())
                .gt(SeatLock::getExpireTime, LocalDateTime.now());

        List<SeatLock> seatLocks = seatLockMapper.selectList(lockWrapper);
        if (seatLocks.isEmpty()) {
            throw new BusinessException("座位锁定已过期或不存在");
        }

        // 获取场次信息
        Long scheduleId = seatLocks.get(0).getScheduleId();
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException("场次不存在");
        }

        // 计算票价总额
        BigDecimal ticketAmount = schedule.getPrice().multiply(new BigDecimal(seatLocks.size()));

        // 计算卖品总额
        BigDecimal snackAmount = BigDecimal.ZERO;
        List<OrderSnack> orderSnacks = new ArrayList<>();

        if (dto.getSnacks() != null && !dto.getSnacks().isEmpty()) {
            for (ComboOrderCreateDTO.SnackItem item : dto.getSnacks()) {
                Snack snack = snackMapper.selectById(item.getSnackId());
                if (snack == null) {
                    throw new BusinessException("卖品不存在：" + item.getSnackId());
                }

                BigDecimal itemTotal = snack.getPrice().multiply(new BigDecimal(item.getQuantity()));
                snackAmount = snackAmount.add(itemTotal);

                OrderSnack orderSnack = new OrderSnack();
                orderSnack.setSnackId(snack.getId());
                orderSnack.setSnackName(snack.getName());
                orderSnack.setSpec(item.getSpec());
                orderSnack.setQuantity(item.getQuantity());
                orderSnack.setPrice(snack.getPrice());
                orderSnack.setTotalPrice(itemTotal);
                orderSnack.setPickCode(generatePickCode());
                orderSnack.setStatus(1); // 未取货
                orderSnacks.add(orderSnack);
            }
        }

        BigDecimal totalAmount = ticketAmount.add(snackAmount);
        BigDecimal discountAmount = BigDecimal.ZERO;
        UserCoupon userCoupon = null;

        // 处理优惠券
        if (dto.getUserCouponId() != null) {
            userCoupon = validateAndUseCoupon(dto.getUserCouponId(), userId, totalAmount);
            discountAmount = calculateDiscount(userCoupon, totalAmount);
        }

        // 计算实付金额
        BigDecimal payAmount = totalAmount.subtract(discountAmount);
        if (payAmount.compareTo(BigDecimal.ZERO) < 0) {
            payAmount = BigDecimal.ZERO;
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setType(OrderType.COMBO.getCode());
        order.setCinemaId(schedule.getCinemaId());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setUserCouponId(dto.getUserCouponId());
        order.setStatus(OrderStatus.UNPAID.getCode());
        order.setExpireTime(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
        orderMapper.insert(order);

        // 创建票务明细
        for (SeatLock seatLock : seatLocks) {
            OrderTicket orderTicket = new OrderTicket();
            orderTicket.setOrderId(order.getId());
            orderTicket.setScheduleId(scheduleId);
            orderTicket.setSeatId(seatLock.getSeatId());
            orderTicket.setSeatNo(seatLock.getSeatNo());
            orderTicket.setTicketPrice(schedule.getPrice());
            orderTicket.setStatus(1); // 未取票
            orderTicketMapper.insert(orderTicket);

            // 更新座位锁定记录
            seatLock.setOrderId(order.getId());
            seatLockMapper.updateById(seatLock);
        }

        // 创建卖品明细
        for (OrderSnack orderSnack : orderSnacks) {
            orderSnack.setOrderId(order.getId());
            orderSnackMapper.insert(orderSnack);
        }

        // 更新优惠券状态
        if (userCoupon != null) {
            userCoupon.setStatus(2); // 已使用
            userCoupon.setUseTime(LocalDateTime.now());
            userCoupon.setOrderId(order.getId());
            userCouponMapper.updateById(userCoupon);
        }

        // 构建返回结果
        OrderCreateVO vo = new OrderCreateVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(totalAmount);
        vo.setDiscountAmount(discountAmount);
        vo.setPayAmount(payAmount);
        vo.setExpireTime(order.getExpireTime());
        vo.setPayInfo(buildMockPayInfo(order));

        return vo;
    }

    @Override
    public PayInfoVO getPayInfo(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }
        if (!OrderStatus.UNPAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }

        PayInfoVO vo = new PayInfoVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setPayAmount(order.getPayAmount());
        vo.setExpireTime(order.getExpireTime());
        vo.setPayInfo(buildMockPayInfo(order));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (!OrderStatus.UNPAID.getCode().equals(order.getStatus())) {
            throw new BusinessException("只能取消待支付订单");
        }

        // 更新订单状态
        order.setStatus(OrderStatus.CANCELLED.getCode());
        orderMapper.updateById(order);

        // 释放座位锁定
        releaseSeatLocks(orderId);

        // 退还优惠券
        if (order.getUserCouponId() != null) {
            returnCoupon(order.getUserCouponId());
        }
    }

    @Override
    public Page<OrderListItemVO> getOrderList(OrderQueryDTO dto, Long userId) {
        Page<Order> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus())
                .eq(dto.getType() != null, Order::getType, dto.getType())
                .orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<OrderListItemVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderListItemVO> voList = orderPage.getRecords().stream()
                .map(this::convertToListItemVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public OrderDetailVO getOrderDetail(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }

        return buildOrderDetailVO(order);
    }

    @Override
    public PickCodeVO getPickCode(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }
        if (!OrderStatus.TICKETED.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单未出票，无法获取取票码");
        }

        // 查询票务明细
        LambdaQueryWrapper<OrderTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderTicket::getOrderId, orderId);
        List<OrderTicket> tickets = orderTicketMapper.selectList(wrapper);

        if (tickets.isEmpty()) {
            throw new BusinessException("票务信息不存在");
        }

        PickCodeVO vo = new PickCodeVO();
        vo.setPickCode(tickets.get(0).getPickCode());
        vo.setPickQrcode(tickets.get(0).getPickQrcode());

        // 获取影厅名称
        Long scheduleId = tickets.get(0).getScheduleId();
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        Hall hall = hallMapper.selectById(schedule.getHallId());

        List<PickCodeVO.TicketItem> ticketItems = tickets.stream()
                .map(t -> {
                    PickCodeVO.TicketItem item = new PickCodeVO.TicketItem();
                    item.setSeatNo(t.getSeatNo());
                    item.setHallName(hall != null ? hall.getName() : "");
                    return item;
                })
                .collect(Collectors.toList());
        vo.setTickets(ticketItems);

        return vo;
    }

    @Override
    public RefundCheckVO checkRefund(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }

        RefundCheckVO vo = new RefundCheckVO();

        // 检查订单状态
        if (!OrderStatus.PAID.getCode().equals(order.getStatus()) &&
            !OrderStatus.TICKETED.getCode().equals(order.getStatus())) {
            vo.setCanRefund(false);
            vo.setReason("订单状态不允许退票");
            return vo;
        }

        // 检查开场时间
        if (!canRefundByShowTime(orderId)) {
            vo.setCanRefund(false);
            vo.setReason("开场前2小时内不可退票");
            return vo;
        }

        // 计算退款金额
        BigDecimal payAmount = order.getPayAmount();
        BigDecimal refundFee = payAmount.multiply(REFUND_FEE_RATE).setScale(2, RoundingMode.HALF_UP);
        BigDecimal actualRefund = payAmount.subtract(refundFee);

        // 如果开场前24小时以上，无手续费
        if (isRefundBefore24Hours(orderId)) {
            refundFee = BigDecimal.ZERO;
            actualRefund = payAmount;
        }

        vo.setCanRefund(true);
        vo.setRefundAmount(payAmount);
        vo.setRefundFee(refundFee);
        vo.setActualRefund(actualRefund);
        vo.setReason("开场前退票收取10%手续费");

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundApplyVO applyRefund(Long orderId, RefundApplyDTO dto, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }

        // 检查是否可退票
        RefundCheckVO checkVO = checkRefund(orderId, userId);
        if (!checkVO.getCanRefund()) {
            throw new BusinessException(checkVO.getReason());
        }

        // 更新订单状态为退款中
        order.setStatus(OrderStatus.REFUNDING.getCode());
        order.setRefundReason(dto.getReason());
        order.setRefundAmount(checkVO.getActualRefund());
        order.setRefundFee(checkVO.getRefundFee());
        orderMapper.updateById(order);

        RefundApplyVO vo = new RefundApplyVO();
        vo.setRefundId(orderId);
        vo.setRefundAmount(checkVO.getActualRefund());
        vo.setRefundFee(checkVO.getRefundFee());
        vo.setStatus(order.getStatus());
        vo.setMessage("退票申请已提交，等待审核");

        return vo;
    }

    @Override
    public SnackPickCodeVO getSnackPickCode(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权访问该订单");
        }

        // 查询卖品明细
        LambdaQueryWrapper<OrderSnack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderSnack::getOrderId, orderId);
        List<OrderSnack> snacks = orderSnackMapper.selectList(wrapper);

        if (snacks.isEmpty()) {
            throw new BusinessException("卖品信息不存在");
        }

        SnackPickCodeVO vo = new SnackPickCodeVO();
        vo.setPickCode(snacks.get(0).getPickCode());

        List<SnackPickCodeVO.SnackItem> snackItems = snacks.stream()
                .map(s -> {
                    SnackPickCodeVO.SnackItem item = new SnackPickCodeVO.SnackItem();
                    item.setName(s.getSnackName());
                    item.setSpec(s.getSpec());
                    item.setQuantity(s.getQuantity());
                    return item;
                })
                .collect(Collectors.toList());
        vo.setSnacks(snackItems);

        return vo;
    }

    @Override
    public Page<OrderListItemVO> getAdminOrderList(AdminOrderQueryDTO dto) {
        Page<Order> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(dto.getOrderNo() != null && !dto.getOrderNo().isEmpty(), Order::getOrderNo, dto.getOrderNo())
                .eq(dto.getStatus() != null, Order::getStatus, dto.getStatus())
                .eq(dto.getType() != null, Order::getType, dto.getType())
                .eq(dto.getUserId() != null, Order::getUserId, dto.getUserId())
                .eq(dto.getCinemaId() != null, Order::getCinemaId, dto.getCinemaId())
                .ge(dto.getStartTime() != null, Order::getCreateTime, dto.getStartTime())
                .le(dto.getEndTime() != null, Order::getCreateTime, dto.getEndTime())
                .orderByDesc(Order::getCreateTime);

        Page<Order> orderPage = orderMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<OrderListItemVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderListItemVO> voList = orderPage.getRecords().stream()
                .map(this::convertToListItemVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public OrderDetailVO getAdminOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }

        return buildOrderDetailVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RefundAuditVO auditRefund(Long orderId, RefundAuditDTO dto, Integer adminId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatus.REFUNDING.getCode().equals(order.getStatus())) {
            throw new BusinessException("订单不在退款中状态");
        }

        RefundAuditVO vo = new RefundAuditVO();

        if (dto.getApprove()) {
            // 同意退款
            order.setStatus(OrderStatus.REFUNDED.getCode());
            order.setRefundTime(LocalDateTime.now());
            order.setRefundAuditorId(adminId);
            order.setRefundAuditTime(LocalDateTime.now());
            order.setRefundAuditRemark(dto.getRemark());
            orderMapper.updateById(order);

            // 释放座位锁定状态为已售
            releaseSeatLocksForRefund(orderId);

            vo.setRefundAmount(order.getRefundAmount());
            vo.setRefundFee(order.getRefundFee());
        } else {
            // 拒绝退款，恢复原状态
            order.setStatus(OrderStatus.PAID.getCode());
            order.setRefundAuditorId(adminId);
            order.setRefundAuditTime(LocalDateTime.now());
            order.setRefundAuditRemark(dto.getRemark());
            order.setRefundAmount(null);
            order.setRefundFee(null);
            order.setRefundReason(null);
            orderMapper.updateById(order);

            vo.setRefundAmount(BigDecimal.ZERO);
            vo.setRefundFee(BigDecimal.ZERO);
        }

        return vo;
    }

    // ==================== 私有方法 ====================

    /**
     * 解析锁定ID（格式：lock_scheduleId_seatId1-seatId2）
     */
    private List<Long> parseLockId(String lockId) {
        if (lockId == null || !lockId.startsWith("lock_")) {
            throw new BusinessException("无效的锁定ID");
        }

        String[] parts = lockId.split("_");
        if (parts.length < 3) {
            throw new BusinessException("无效的锁定ID格式");
        }

        String[] seatIdStrs = parts[2].split("-");
        return Arrays.stream(seatIdStrs)
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    /**
     * 生成订单号
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = String.format("%04d", new Random().nextInt(10000));
        return timestamp + random;
    }

    /**
     * 生成取票码（6位数字）
     */
    private String generatePickCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }

    /**
     * 验证并获取优惠券
     */
    private UserCoupon validateAndUseCoupon(Long userCouponId, Long userId, BigDecimal totalAmount) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null) {
            throw new BusinessException("优惠券不存在");
        }
        if (!userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不属于当前用户");
        }
        if (userCoupon.getStatus() != 1) {
            throw new BusinessException("优惠券已使用或已过期");
        }
        if (userCoupon.getMinAmount() != null && totalAmount.compareTo(userCoupon.getMinAmount()) < 0) {
            throw new BusinessException("订单金额不满足优惠券使用条件");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(userCoupon.getValidStart()) || now.isAfter(userCoupon.getValidEnd())) {
            throw new BusinessException("优惠券已过期");
        }

        return userCoupon;
    }

    /**
     * 计算优惠金额
     */
    private BigDecimal calculateDiscount(UserCoupon userCoupon, BigDecimal totalAmount) {
        if (userCoupon == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        Integer type = userCoupon.getType();
        BigDecimal value = userCoupon.getValue();

        if (type == 1) {
            // 满减券
            discount = value;
        } else if (type == 2) {
            // 折扣券
            discount = totalAmount.subtract(totalAmount.multiply(value).setScale(2, RoundingMode.HALF_UP));
        } else if (type == 3) {
            // 立减券
            discount = value;
        } else if (type == 4) {
            // 兑换券
            discount = totalAmount;
        }

        if (discount.compareTo(totalAmount) > 0) {
            discount = totalAmount;
        }

        return discount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 构建模拟支付信息
     */
    private Map<String, Object> buildMockPayInfo(Order order) {
        Map<String, Object> payInfo = new HashMap<>();
        payInfo.put("appId", "wxMockAppId");
        payInfo.put("timeStamp", String.valueOf(System.currentTimeMillis() / 1000));
        payInfo.put("nonceStr", UUID.randomUUID().toString().replace("-", ""));
        payInfo.put("package", "prepay_id=mock_prepay_id_" + order.getOrderNo());
        payInfo.put("signType", "RSA");
        payInfo.put("paySign", "mock_pay_sign");
        return payInfo;
    }

    /**
     * 释放座位锁定
     */
    private void releaseSeatLocks(Long orderId) {
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(OrderTicket::getOrderId, orderId);
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        for (OrderTicket ticket : tickets) {
            // 更新座位锁定状态为释放
            LambdaUpdateWrapper<SeatLock> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SeatLock::getScheduleId, ticket.getScheduleId())
                    .eq(SeatLock::getSeatId, ticket.getSeatId())
                    .set(SeatLock::getStatus, SeatLockStatus.RELEASED.getCode());
            seatLockMapper.update(null, updateWrapper);
        }
    }

    /**
     * 退款时释放座位锁定
     */
    private void releaseSeatLocksForRefund(Long orderId) {
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(OrderTicket::getOrderId, orderId);
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        for (OrderTicket ticket : tickets) {
            // 更新座位锁定状态为释放
            LambdaUpdateWrapper<SeatLock> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SeatLock::getScheduleId, ticket.getScheduleId())
                    .eq(SeatLock::getSeatId, ticket.getSeatId())
                    .set(SeatLock::getStatus, SeatLockStatus.RELEASED.getCode());
            seatLockMapper.update(null, updateWrapper);
        }
    }

    /**
     * 退还优惠券
     */
    private void returnCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon != null) {
            userCoupon.setStatus(1); // 未使用
            userCoupon.setUseTime(null);
            userCoupon.setOrderId(null);
            userCouponMapper.updateById(userCoupon);
        }
    }

    /**
     * 检查是否可以退票（开场前2小时）
     */
    private boolean canRefundByShowTime(Long orderId) {
        LambdaQueryWrapper<OrderTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderTicket::getOrderId, orderId);
        wrapper.last("LIMIT 1");
        OrderTicket ticket = orderTicketMapper.selectOne(wrapper);

        if (ticket == null) {
            return false;
        }

        Schedule schedule = scheduleMapper.selectById(ticket.getScheduleId());
        if (schedule == null) {
            return false;
        }

        LocalDateTime showDateTime = LocalDateTime.of(schedule.getShowDate(), schedule.getShowTime());
        LocalDateTime limitTime = showDateTime.minusHours(REFUND_LIMIT_HOURS);

        return LocalDateTime.now().isBefore(limitTime);
    }

    /**
     * 检查是否在开场前24小时以上
     */
    private boolean isRefundBefore24Hours(Long orderId) {
        LambdaQueryWrapper<OrderTicket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderTicket::getOrderId, orderId);
        wrapper.last("LIMIT 1");
        OrderTicket ticket = orderTicketMapper.selectOne(wrapper);

        if (ticket == null) {
            return false;
        }

        Schedule schedule = scheduleMapper.selectById(ticket.getScheduleId());
        if (schedule == null) {
            return false;
        }

        LocalDateTime showDateTime = LocalDateTime.of(schedule.getShowDate(), schedule.getShowTime());
        LocalDateTime limitTime = showDateTime.minusHours(24);

        return LocalDateTime.now().isBefore(limitTime);
    }

    /**
     * 转换为列表项VO
     */
    private OrderListItemVO convertToListItemVO(Order order) {
        OrderListItemVO vo = new OrderListItemVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setType(order.getType());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setCreateTime(order.getCreateTime());

        // 查询票务信息
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(OrderTicket::getOrderId, order.getId());
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        if (!tickets.isEmpty()) {
            List<OrderListItemVO.TicketInfo> ticketInfos = new ArrayList<>();

            for (OrderTicket ticket : tickets) {
                Schedule schedule = scheduleMapper.selectById(ticket.getScheduleId());
                if (schedule != null) {
                    Movie movie = movieMapper.selectById(schedule.getMovieId());
                    Cinema cinema = cinemaMapper.selectById(schedule.getCinemaId());
                    Hall hall = hallMapper.selectById(schedule.getHallId());

                    OrderListItemVO.TicketInfo info = new OrderListItemVO.TicketInfo();
                    info.setMovieTitle(movie != null ? movie.getTitle() : "");
                    info.setCinemaName(cinema != null ? cinema.getName() : "");
                    info.setHallName(hall != null ? hall.getName() : "");
                    info.setShowDate(schedule.getShowDate().toString());
                    info.setShowTime(schedule.getShowTime().toString());
                    info.setSeatNo(ticket.getSeatNo());

                    ticketInfos.add(info);
                }
            }

            vo.setTickets(ticketInfos);
        } else {
            vo.setTickets(new ArrayList<>());
        }

        return vo;
    }

    /**
     * 构建订单详情VO
     */
    private OrderDetailVO buildOrderDetailVO(Order order) {
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setType(order.getType());
        vo.setStatus(order.getStatus());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setDiscountAmount(order.getDiscountAmount());
        vo.setPayAmount(order.getPayAmount());
        vo.setPayTime(order.getPayTime());
        vo.setPayType(order.getPayType());
        vo.setCreateTime(order.getCreateTime());
        vo.setExpireTime(order.getExpireTime());

        // 查询票务信息
        LambdaQueryWrapper<OrderTicket> ticketWrapper = new LambdaQueryWrapper<>();
        ticketWrapper.eq(OrderTicket::getOrderId, order.getId());
        List<OrderTicket> tickets = orderTicketMapper.selectList(ticketWrapper);

        if (!tickets.isEmpty()) {
            List<OrderDetailVO.TicketDetail> ticketDetails = new ArrayList<>();

            for (OrderTicket ticket : tickets) {
                Schedule schedule = scheduleMapper.selectById(ticket.getScheduleId());
                if (schedule != null) {
                    Movie movie = movieMapper.selectById(schedule.getMovieId());
                    Cinema cinema = cinemaMapper.selectById(schedule.getCinemaId());
                    Hall hall = hallMapper.selectById(schedule.getHallId());

                    OrderDetailVO.TicketDetail detail = new OrderDetailVO.TicketDetail();
                    detail.setId(ticket.getId());
                    detail.setScheduleId(ticket.getScheduleId());
                    detail.setMovieTitle(movie != null ? movie.getTitle() : "");
                    detail.setMoviePoster(movie != null ? movie.getPoster() : "");
                    detail.setCinemaName(cinema != null ? cinema.getName() : "");
                    detail.setCinemaAddress(cinema != null ? cinema.getAddress() : "");
                    detail.setHallName(hall != null ? hall.getName() : "");
                    detail.setShowDate(schedule.getShowDate().toString());
                    detail.setShowTime(schedule.getShowTime().toString());
                    detail.setSeatNo(ticket.getSeatNo());
                    detail.setTicketPrice(ticket.getTicketPrice());
                    detail.setPickCode(ticket.getPickCode());
                    detail.setPickQrcode(ticket.getPickQrcode());
                    detail.setStatus(ticket.getStatus());

                    ticketDetails.add(detail);
                }
            }

            vo.setTickets(ticketDetails);
        } else {
            vo.setTickets(new ArrayList<>());
        }

        // 查询卖品信息
        LambdaQueryWrapper<OrderSnack> snackWrapper = new LambdaQueryWrapper<>();
        snackWrapper.eq(OrderSnack::getOrderId, order.getId());
        List<OrderSnack> snacks = orderSnackMapper.selectList(snackWrapper);

        if (!snacks.isEmpty()) {
            List<OrderDetailVO.SnackDetail> snackDetails = snacks.stream()
                    .map(s -> {
                        OrderDetailVO.SnackDetail detail = new OrderDetailVO.SnackDetail();
                        detail.setId(s.getId());
                        detail.setSnackName(s.getSnackName());
                        detail.setSpec(s.getSpec());
                        detail.setQuantity(s.getQuantity());
                        detail.setPrice(s.getPrice());
                        detail.setTotalPrice(s.getTotalPrice());
                        detail.setPickCode(s.getPickCode());
                        detail.setStatus(s.getStatus());
                        return detail;
                    })
                    .collect(Collectors.toList());

            vo.setSnacks(snackDetails);
        } else {
            vo.setSnacks(new ArrayList<>());
        }

        // 优惠券信息
        if (order.getUserCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(order.getUserCouponId());
            if (userCoupon != null) {
                OrderDetailVO.CouponInfo couponInfo = new OrderDetailVO.CouponInfo();
                couponInfo.setId(userCoupon.getId());
                couponInfo.setName(userCoupon.getCouponName());
                couponInfo.setValue(userCoupon.getValue());
                vo.setCoupon(couponInfo);
            }
        }

        return vo;
    }

}
