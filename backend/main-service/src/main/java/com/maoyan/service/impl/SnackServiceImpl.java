package com.maoyan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.snack.SnackOrderCreateDTO;
import com.maoyan.dto.snack.SnackQueryDTO;
import com.maoyan.dto.snack.SnackSaveDTO;
import com.maoyan.entity.*;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.*;
import com.maoyan.service.SnackService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.snack.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 卖品服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class SnackServiceImpl implements SnackService {

    private final SnackCategoryMapper snackCategoryMapper;
    private final SnackMapper snackMapper;
    private final OrderMapper orderMapper;
    private final OrderSnackMapper orderSnackMapper;
    private final CinemaMapper cinemaMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;

    @Override
    public List<SnackCategoryVO> getCategories() {
        LambdaQueryWrapper<SnackCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SnackCategory::getStatus, 1)
                .orderByAsc(SnackCategory::getSort);
        List<SnackCategory> categories = snackCategoryMapper.selectList(wrapper);
        return categories.stream().map(this::convertToCategoryVO).collect(Collectors.toList());
    }

    @Override
    public PageVO<SnackListVO> getSnackList(SnackQueryDTO queryDTO) {
        Page<Snack> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Snack::getStatus, 1);
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Snack::getCategoryId, queryDTO.getCategoryId());
        }
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(Snack::getName, queryDTO.getKeyword());
        }
        wrapper.orderByDesc(Snack::getSales)
                .orderByAsc(Snack::getSort);
        Page<Snack> snackPage = snackMapper.selectPage(page, wrapper);

        // 获取分类名称
        Map<Integer, String> categoryNameMap = getCategoryNameMap();

        List<SnackListVO> list = snackPage.getRecords().stream()
                .map(snack -> convertToListVO(snack, categoryNameMap))
                .collect(Collectors.toList());

        return PageVO.of(list, snackPage.getTotal(), (int) snackPage.getCurrent(), (int) snackPage.getSize());
    }

    @Override
    public SnackDetailVO getSnackDetail(Long id) {
        Snack snack = snackMapper.selectById(id);
        if (snack == null) {
            throw new BusinessException("卖品不存在");
        }

        SnackDetailVO vo = new SnackDetailVO();
        BeanUtils.copyProperties(snack, vo);

        // 获取分类名称
        SnackCategory category = snackCategoryMapper.selectById(snack.getCategoryId());
        if (category != null) {
            vo.setCategoryName(category.getName());
        }

        // 解析规格
        if (StrUtil.isNotBlank(snack.getSpecs())) {
            List<SnackDetailVO.SnackSpecDetailVO> specs = JSONUtil.toList(snack.getSpecs(), SnackDetailVO.SnackSpecDetailVO.class);
            vo.setSpecs(specs);
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SnackOrderCreateVO createSnackOrder(SnackOrderCreateDTO dto, Long userId) {
        // 验证影院
        Cinema cinema = cinemaMapper.selectById(dto.getCinemaId());
        if (cinema == null || cinema.getStatus() != 1) {
            throw new BusinessException("影院不存在或已禁用");
        }

        // 验证卖品和库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderSnack> orderSnacks = new ArrayList<>();

        for (SnackOrderCreateDTO.SnackItem item : dto.getItems()) {
            Snack snack = snackMapper.selectById(item.getSnackId());
            if (snack == null || snack.getStatus() != 1) {
                throw new BusinessException("卖品不存在或已下架：" + item.getSnackId());
            }

            // 检查库存
            if (snack.getStock() < item.getQuantity()) {
                throw new BusinessException("库存不足：" + snack.getName());
            }

            // 获取规格价格
            BigDecimal price = getPriceBySpec(snack, item.getSpec());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // 创建订单项
            OrderSnack orderSnack = new OrderSnack();
            orderSnack.setSnackId(snack.getId());
            orderSnack.setSnackName(snack.getName());
            orderSnack.setSpec(item.getSpec());
            orderSnack.setQuantity(item.getQuantity());
            orderSnack.setPrice(price);
            orderSnack.setTotalPrice(itemTotal);
            orderSnack.setStatus(1);
            orderSnacks.add(orderSnack);

            // 扣减库存
            snack.setStock(snack.getStock() - item.getQuantity());
            snackMapper.updateById(snack);
        }

        // 处理优惠券
        BigDecimal discountAmount = BigDecimal.ZERO;
        Long userCouponId = dto.getUserCouponId();
        if (userCouponId != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
            if (userCoupon == null || !userCoupon.getUserId().equals(userId) || userCoupon.getStatus() != 1) {
                throw new BusinessException("优惠券不可用");
            }
            Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
            if (coupon == null) {
                throw new BusinessException("优惠券不存在");
            }
            if (totalAmount.compareTo(coupon.getMinAmount()) >= 0) {
                discountAmount = coupon.getValue();
                // 使用优惠券
                userCoupon.setStatus(2);
                userCouponMapper.updateById(userCoupon);
            }
        }

        BigDecimal payAmount = totalAmount.subtract(discountAmount);

        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setType(2); // 卖品订单
        order.setCinemaId(dto.getCinemaId());
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(discountAmount);
        order.setPayAmount(payAmount);
        order.setUserCouponId(userCouponId);
        order.setStatus(1); // 待支付
        order.setExpireTime(LocalDateTime.now().plusMinutes(15));
        orderMapper.insert(order);

        // 创建订单项
        for (OrderSnack orderSnack : orderSnacks) {
            orderSnack.setOrderId(order.getId());
            orderSnack.setPickCode(generatePickCode());
            orderSnackMapper.insert(orderSnack);
        }

        // 构建返回结果
        SnackOrderCreateVO vo = new SnackOrderCreateVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(totalAmount);
        vo.setDiscountAmount(discountAmount);
        vo.setPayAmount(payAmount);
        vo.setExpireTime(order.getExpireTime());
        // 实际支付信息需要调用微信支付API，这里简化处理
        vo.setPayInfo(null);

        return vo;
    }

    @Override
    public SnackOrderDetailVO getSnackOrderDetail(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException("订单不存在");
        }
        return convertToOrderDetailVO(order);
    }

    @Override
    public PageVO<SnackOrderListVO> getSnackOrderList(Integer status, Long userId, Integer page, Integer pageSize) {
        Page<Order> pageParam = new Page<>(page, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .eq(Order::getType, 2); // 卖品订单
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        Page<Order> orderPage = orderMapper.selectPage(pageParam, wrapper);

        // 统计订单项数量
        List<Long> orderIds = orderPage.getRecords().stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        Map<Long, Integer> itemCountMap = getOrderItemCountMap(orderIds);

        List<SnackOrderListVO> list = orderPage.getRecords().stream()
                .map(order -> convertToOrderListVO(order, itemCountMap))
                .collect(Collectors.toList());

        return PageVO.of(list, orderPage.getTotal(), (int) orderPage.getCurrent(), (int) orderPage.getSize());
    }

    @Override
    public PageVO<SnackListVO> getSnackListAdmin(SnackQueryDTO queryDTO) {
        Page<Snack> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<Snack> wrapper = new LambdaQueryWrapper<>();
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Snack::getCategoryId, queryDTO.getCategoryId());
        }
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.like(Snack::getName, queryDTO.getKeyword());
        }
        wrapper.orderByDesc(Snack::getCreateTime);
        Page<Snack> snackPage = snackMapper.selectPage(page, wrapper);

        Map<Integer, String> categoryNameMap = getCategoryNameMap();

        List<SnackListVO> list = snackPage.getRecords().stream()
                .map(snack -> convertToListVO(snack, categoryNameMap))
                .collect(Collectors.toList());

        return PageVO.of(list, snackPage.getTotal(), (int) snackPage.getCurrent(), (int) snackPage.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSnack(SnackSaveDTO dto) {
        Snack snack = new Snack();
        BeanUtils.copyProperties(dto, snack);
        if (CollUtil.isNotEmpty(dto.getSpecs())) {
            snack.setSpecs(JSONUtil.toJsonStr(dto.getSpecs()));
        }
        snackMapper.insert(snack);
        return snack.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSnack(Long id, SnackSaveDTO dto) {
        Snack snack = snackMapper.selectById(id);
        if (snack == null) {
            throw new BusinessException("卖品不存在");
        }
        BeanUtils.copyProperties(dto, snack, "id");
        if (CollUtil.isNotEmpty(dto.getSpecs())) {
            snack.setSpecs(JSONUtil.toJsonStr(dto.getSpecs()));
        }
        snackMapper.updateById(snack);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSnack(Long id) {
        snackMapper.deleteById(id);
    }

    // ===== 私有方法 =====

    private SnackCategoryVO convertToCategoryVO(SnackCategory category) {
        SnackCategoryVO vo = new SnackCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }

    private SnackListVO convertToListVO(Snack snack, Map<Integer, String> categoryNameMap) {
        SnackListVO vo = new SnackListVO();
        BeanUtils.copyProperties(snack, vo);
        vo.setCategoryName(categoryNameMap.get(snack.getCategoryId()));
        if (StrUtil.isNotBlank(snack.getSpecs())) {
            List<SnackListVO.SnackSpecVO> specs = JSONUtil.toList(snack.getSpecs(), SnackListVO.SnackSpecVO.class);
            vo.setSpecs(specs);
        }
        return vo;
    }

    private Map<Integer, String> getCategoryNameMap() {
        List<SnackCategory> categories = snackCategoryMapper.selectList(null);
        return categories.stream()
                .collect(Collectors.toMap(SnackCategory::getId, SnackCategory::getName));
    }

    private BigDecimal getPriceBySpec(Snack snack, String spec) {
        if (StrUtil.isBlank(snack.getSpecs())) {
            return snack.getPrice();
        }
        List<SnackSaveDTO.SnackSpec> specs = JSONUtil.toList(snack.getSpecs(), SnackSaveDTO.SnackSpec.class);
        return specs.stream()
                .filter(s -> s.getName().equals(spec))
                .findFirst()
                .map(SnackSaveDTO.SnackSpec::getPrice)
                .orElse(snack.getPrice());
    }

    private String generateOrderNo() {
        return "SN" + IdUtil.getSnowflakeNextIdStr();
    }

    private String generatePickCode() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
    }

    private SnackOrderDetailVO convertToOrderDetailVO(Order order) {
        SnackOrderDetailVO vo = new SnackOrderDetailVO();
        BeanUtils.copyProperties(order, vo);

        // 获取影院信息
        Cinema cinema = cinemaMapper.selectById(order.getCinemaId());
        if (cinema != null) {
            vo.setCinemaName(cinema.getName());
            vo.setCinemaAddress(cinema.getAddress());
        }

        // 获取订单项
        LambdaQueryWrapper<OrderSnack> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderSnack::getOrderId, order.getId());
        List<OrderSnack> orderSnacks = orderSnackMapper.selectList(wrapper);
        List<SnackOrderDetailVO.OrderItemVO> items = orderSnacks.stream()
                .map(this::convertToOrderItemVO)
                .collect(Collectors.toList());
        vo.setItems(items);

        // 获取优惠券信息
        if (order.getUserCouponId() != null) {
            UserCoupon userCoupon = userCouponMapper.selectById(order.getUserCouponId());
            if (userCoupon != null) {
                Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
                if (coupon != null) {
                    SnackOrderDetailVO.CouponInfo couponInfo = new SnackOrderDetailVO.CouponInfo();
                    couponInfo.setId(coupon.getId());
                    couponInfo.setName(coupon.getName());
                    couponInfo.setValue(coupon.getValue());
                    vo.setCoupon(couponInfo);
                }
            }
        }

        return vo;
    }

    private SnackOrderDetailVO.OrderItemVO convertToOrderItemVO(OrderSnack orderSnack) {
        SnackOrderDetailVO.OrderItemVO vo = new SnackOrderDetailVO.OrderItemVO();
        BeanUtils.copyProperties(orderSnack, vo);
        return vo;
    }

    private Map<Long, Integer> getOrderItemCountMap(List<Long> orderIds) {
        if (CollUtil.isEmpty(orderIds)) {
            return Map.of();
        }
        LambdaQueryWrapper<OrderSnack> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OrderSnack::getOrderId, orderIds);
        List<OrderSnack> orderSnacks = orderSnackMapper.selectList(wrapper);
        return orderSnacks.stream()
                .collect(Collectors.groupingBy(OrderSnack::getOrderId, Collectors.summingInt(x -> 1)));
    }

    private SnackOrderListVO convertToOrderListVO(Order order, Map<Long, Integer> itemCountMap) {
        SnackOrderListVO vo = new SnackOrderListVO();
        BeanUtils.copyProperties(order, vo);
        vo.setItemCount(itemCountMap.getOrDefault(order.getId(), 0));

        // 获取影院名称
        Cinema cinema = cinemaMapper.selectById(order.getCinemaId());
        if (cinema != null) {
            vo.setCinemaName(cinema.getName());
        }

        return vo;
    }
}
