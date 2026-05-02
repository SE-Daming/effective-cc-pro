package com.maoyan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.ResultCode;
import com.maoyan.common.enums.CouponStatus;
import com.maoyan.common.enums.CouponType;
import com.maoyan.dto.coupon.*;
import com.maoyan.entity.Coupon;
import com.maoyan.entity.User;
import com.maoyan.entity.UserCoupon;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.CouponMapper;
import com.maoyan.mapper.UserCouponMapper;
import com.maoyan.mapper.UserMapper;
import com.maoyan.service.CouponService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.coupon.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 优惠券服务实现
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;
    private final UserMapper userMapper;

    // ==================== C端接口实现 ====================

    @Override
    public PageVO<AvailableCouponVO> getAvailableCoupons(Long userId, AvailableCouponQueryDTO query) {
        // 查询启用状态且在有效期内的优惠券
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
                .eq(Coupon::getIsDeleted, 0)
                .and(w -> w.isNull(Coupon::getValidEnd)
                        .or()
                        .ge(Coupon::getValidEnd, LocalDate.now()))
                .orderByDesc(Coupon::getCreateTime);

        IPage<Coupon> page = couponMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                wrapper
        );

        // 查询用户已领取的优惠券
        Set<Long> receivedCouponIds = getReceivedCouponIds(userId);

        List<AvailableCouponVO> list = page.getRecords().stream()
                .map(coupon -> convertToAvailableCouponVO(coupon, receivedCouponIds.contains(coupon.getId())))
                .collect(Collectors.toList());

        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReceiveCouponVO receiveCoupon(Long userId, Long couponId) {
        // 查询优惠券
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_FOUND);
        }

        // 检查优惠券状态
        if (coupon.getStatus() != 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_AVAILABLE);
        }

        // 检查有效期
        if (coupon.getValidEnd() != null && coupon.getValidEnd().isBefore(LocalDate.now())) {
            throw new BusinessException(ResultCode.COUPON_EXPIRED);
        }

        // 检查库存
        if (coupon.getTotalCount() != null && coupon.getReceiveCount() >= coupon.getTotalCount()) {
            throw new BusinessException(ResultCode.COUPON_NOT_AVAILABLE.getCode(), "优惠券已领完");
        }

        // 检查用户领取数量
        LambdaQueryWrapper<UserCoupon> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getCouponId, couponId);
        Long receivedCount = userCouponMapper.selectCount(countWrapper);
        if (receivedCount >= coupon.getLimitPerUser()) {
            throw new BusinessException(ResultCode.COUPON_LIMIT_EXCEEDED);
        }

        // 计算有效期
        LocalDateTime validStart;
        LocalDateTime validEnd;
        if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            // 从领取开始计算
            validStart = LocalDateTime.now();
            validEnd = validStart.plusDays(coupon.getValidDays());
        } else {
            // 使用固定有效期
            validStart = coupon.getValidStart() != null
                    ? coupon.getValidStart().atStartOfDay()
                    : LocalDateTime.now();
            validEnd = coupon.getValidEnd() != null
                    ? coupon.getValidEnd().atTime(LocalTime.MAX)
                    : validStart.plusYears(1);
        }

        // 创建用户优惠券
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponName(coupon.getName());
        userCoupon.setType(coupon.getType());
        userCoupon.setValue(coupon.getValue());
        userCoupon.setMinAmount(coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO);
        userCoupon.setValidStart(validStart);
        userCoupon.setValidEnd(validEnd);
        userCoupon.setStatus(CouponStatus.UNUSED.getCode());

        userCouponMapper.insert(userCoupon);

        // 更新优惠券领取数量
        coupon.setReceiveCount(coupon.getReceiveCount() + 1);
        couponMapper.updateById(coupon);

        // 返回结果
        ReceiveCouponVO vo = new ReceiveCouponVO();
        vo.setUserCouponId(userCoupon.getId());
        vo.setName(coupon.getName());
        vo.setValidStart(validStart);
        vo.setValidEnd(validEnd);

        return vo;
    }

    @Override
    public CouponDetailVO getCouponDetail(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_FOUND);
        }

        return convertToCouponDetailVO(coupon);
    }

    @Override
    public PageVO<UserCouponVO> getUserCoupons(Long userId, UserCouponQueryDTO query) {
        // 先更新过期的优惠券状态
        updateExpiredCoupons(userId);

        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (query.getStatus() != null) {
            wrapper.eq(UserCoupon::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(UserCoupon::getCreateTime);

        IPage<UserCoupon> page = userCouponMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                wrapper
        );

        List<UserCouponVO> list = page.getRecords().stream()
                .map(this::convertToUserCouponVO)
                .collect(Collectors.toList());

        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    public UsableCouponListVO getUsableCoupons(Long userId, UsableCouponQueryDTO query) {
        // 先更新过期的优惠券状态
        updateExpiredCoupons(userId);

        // 查询用户未使用的优惠券
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, CouponStatus.UNUSED.getCode())
                .ge(UserCoupon::getValidEnd, LocalDateTime.now())
                .orderByAsc(UserCoupon::getValidEnd);

        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);

        // 过滤可用优惠券并计算优惠金额
        List<UsableCouponVO> usableList = new ArrayList<>();
        UsableCouponVO bestCoupon = null;
        BigDecimal maxDiscount = BigDecimal.ZERO;

        for (UserCoupon uc : userCoupons) {
            // 检查是否满足使用条件
            if (!isCouponUsable(uc, query.getAmount(), query.getMovieId(), query.getCinemaId())) {
                continue;
            }

            // 计算优惠金额
            BigDecimal discountAmount = calculateDiscountAmount(uc, query.getAmount());

            UsableCouponVO vo = new UsableCouponVO();
            vo.setId(uc.getId());
            vo.setName(uc.getCouponName());
            vo.setType(uc.getType());
            vo.setTypeName(CouponType.getByCode(uc.getType()).getDesc());
            vo.setValue(uc.getValue());
            vo.setMinAmount(uc.getMinAmount());
            vo.setDiscountAmount(discountAmount);
            vo.setValidEnd(uc.getValidEnd());

            usableList.add(vo);

            // 找出最优优惠券
            if (discountAmount.compareTo(maxDiscount) > 0) {
                maxDiscount = discountAmount;
                bestCoupon = vo;
            }
        }

        UsableCouponListVO result = new UsableCouponListVO();
        result.setList(usableList);

        if (bestCoupon != null) {
            BestCouponVO bestVO = new BestCouponVO();
            bestVO.setId(bestCoupon.getId());
            bestVO.setName(bestCoupon.getName());
            bestVO.setDiscountAmount(bestCoupon.getDiscountAmount());
            result.setBestCoupon(bestVO);
        }

        return result;
    }

    // ==================== B端管理接口实现 ====================

    @Override
    public PageVO<AdminCouponVO> getAdminCouponList(AdminCouponQueryDTO query) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getIsDeleted, 0);

        if (StrUtil.isNotBlank(query.getKeyword())) {
            wrapper.like(Coupon::getName, query.getKeyword());
        }
        if (query.getType() != null) {
            wrapper.eq(Coupon::getType, query.getType());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Coupon::getStatus, query.getStatus());
        }

        wrapper.orderByDesc(Coupon::getCreateTime);

        IPage<Coupon> page = couponMapper.selectPage(
                new Page<>(query.getPage(), query.getPageSize()),
                wrapper
        );

        List<AdminCouponVO> list = page.getRecords().stream()
                .map(this::convertToAdminCouponVO)
                .collect(Collectors.toList());

        return PageVO.of(list, page.getTotal(), (int) page.getCurrent(), (int) page.getSize());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCoupon(CouponSaveDTO dto) {
        // 验证参数
        validateCouponSaveDTO(dto);

        Coupon coupon = new Coupon();
        coupon.setName(dto.getName());
        coupon.setType(dto.getType());
        coupon.setValue(dto.getValue());
        coupon.setMinAmount(dto.getMinAmount() != null ? dto.getMinAmount() : BigDecimal.ZERO);
        coupon.setTotalCount(dto.getTotalCount());
        coupon.setUsedCount(0);
        coupon.setReceiveCount(0);
        coupon.setLimitPerUser(dto.getLimitPerUser());
        coupon.setValidDays(dto.getValidDays());
        coupon.setValidStart(dto.getValidStart());
        coupon.setValidEnd(dto.getValidEnd());
        coupon.setScope(dto.getScope());
        coupon.setScopeIds(dto.getScopeIds());
        coupon.setStatus(dto.getStatus() != null ? dto.getStatus() : 1);

        couponMapper.insert(coupon);
        return coupon.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCoupon(Long couponId, CouponSaveDTO dto) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_FOUND);
        }

        // 验证参数
        validateCouponSaveDTO(dto);

        coupon.setName(dto.getName());
        coupon.setType(dto.getType());
        coupon.setValue(dto.getValue());
        coupon.setMinAmount(dto.getMinAmount() != null ? dto.getMinAmount() : BigDecimal.ZERO);
        coupon.setTotalCount(dto.getTotalCount());
        coupon.setLimitPerUser(dto.getLimitPerUser());
        coupon.setValidDays(dto.getValidDays());
        coupon.setValidStart(dto.getValidStart());
        coupon.setValidEnd(dto.getValidEnd());
        coupon.setScope(dto.getScope());
        coupon.setScopeIds(dto.getScopeIds());
        if (dto.getStatus() != null) {
            coupon.setStatus(dto.getStatus());
        }

        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCoupon(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_FOUND);
        }

        coupon.setIsDeleted(1);
        couponMapper.updateById(coupon);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer distributeCoupon(Long couponId, CouponDistributeDTO dto) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_FOUND);
        }

        if (coupon.getStatus() != 1) {
            throw new BusinessException(ResultCode.COUPON_NOT_AVAILABLE);
        }

        List<Long> userIds;
        if (Boolean.TRUE.equals(dto.getAll())) {
            // 发放给所有用户
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getStatus, 1);
            List<User> users = userMapper.selectList(wrapper);
            userIds = users.stream().map(User::getId).collect(Collectors.toList());
        } else if (CollUtil.isNotEmpty(dto.getUserIds())) {
            userIds = dto.getUserIds();
        } else {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请指定发放用户");
        }

        // 计算有效期
        LocalDateTime validStart;
        LocalDateTime validEnd;
        if (coupon.getValidDays() != null && coupon.getValidDays() > 0) {
            validStart = LocalDateTime.now();
            validEnd = validStart.plusDays(coupon.getValidDays());
        } else {
            validStart = coupon.getValidStart() != null
                    ? coupon.getValidStart().atStartOfDay()
                    : LocalDateTime.now();
            validEnd = coupon.getValidEnd() != null
                    ? coupon.getValidEnd().atTime(LocalTime.MAX)
                    : validStart.plusYears(1);
        }

        // 批量发放
        int count = 0;
        for (Long userId : userIds) {
            // 检查是否已发放
            LambdaQueryWrapper<UserCoupon> checkWrapper = new LambdaQueryWrapper<>();
            checkWrapper.eq(UserCoupon::getUserId, userId)
                    .eq(UserCoupon::getCouponId, couponId);
            if (userCouponMapper.selectCount(checkWrapper) > 0) {
                continue;
            }

            UserCoupon userCoupon = new UserCoupon();
            userCoupon.setUserId(userId);
            userCoupon.setCouponId(couponId);
            userCoupon.setCouponName(coupon.getName());
            userCoupon.setType(coupon.getType());
            userCoupon.setValue(coupon.getValue());
            userCoupon.setMinAmount(coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO);
            userCoupon.setValidStart(validStart);
            userCoupon.setValidEnd(validEnd);
            userCoupon.setStatus(CouponStatus.UNUSED.getCode());

            userCouponMapper.insert(userCoupon);
            count++;
        }

        // 更新领取数量
        if (count > 0) {
            coupon.setReceiveCount(coupon.getReceiveCount() + count);
            couponMapper.updateById(coupon);
        }

        return count;
    }

    // ==================== 私有方法 ====================

    /**
     * 获取用户已领取的优惠券ID集合
     */
    private Set<Long> getReceivedCouponIds(Long userId) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .select(UserCoupon::getCouponId);
        List<UserCoupon> userCoupons = userCouponMapper.selectList(wrapper);
        return userCoupons.stream()
                .map(UserCoupon::getCouponId)
                .collect(Collectors.toSet());
    }

    /**
     * 更新过期的优惠券状态
     */
    private void updateExpiredCoupons(Long userId) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId)
                .eq(UserCoupon::getStatus, CouponStatus.UNUSED.getCode())
                .lt(UserCoupon::getValidEnd, LocalDateTime.now());

        UserCoupon update = new UserCoupon();
        update.setStatus(CouponStatus.EXPIRED.getCode());

        userCouponMapper.update(update, wrapper);
    }

    /**
     * 检查优惠券是否可用
     */
    private boolean isCouponUsable(UserCoupon userCoupon, BigDecimal amount, Long movieId, Long cinemaId) {
        // 检查最低消费
        if (userCoupon.getMinAmount() != null && amount.compareTo(userCoupon.getMinAmount()) < 0) {
            return false;
        }

        // 查询优惠券模板获取适用范围
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null) {
            return false;
        }

        // 检查适用范围
        if (coupon.getScope() != null && coupon.getScope() != 1) {
            if (StrUtil.isBlank(coupon.getScopeIds())) {
                return false;
            }
            Set<String> scopeIds = Arrays.stream(coupon.getScopeIds().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());

            if (coupon.getScope() == 2 && movieId != null) {
                // 指定电影
                if (!scopeIds.contains(movieId.toString())) {
                    return false;
                }
            } else if (coupon.getScope() == 3 && cinemaId != null) {
                // 指定影院
                if (!scopeIds.contains(cinemaId.toString())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * 计算优惠金额
     */
    private BigDecimal calculateDiscountAmount(UserCoupon userCoupon, BigDecimal amount) {
        BigDecimal discount = BigDecimal.ZERO;

        switch (CouponType.getByCode(userCoupon.getType())) {
            case FULL_REDUCTION:
                // 满减券
                if (amount.compareTo(userCoupon.getMinAmount()) >= 0) {
                    discount = userCoupon.getValue();
                }
                break;
            case DISCOUNT:
                // 折扣券，value 为折扣（如 0.8 表示 8 折）
                discount = amount.multiply(BigDecimal.ONE.subtract(userCoupon.getValue()))
                        .setScale(2, RoundingMode.HALF_UP);
                break;
            case INSTANT:
                // 立减券
                discount = userCoupon.getValue();
                break;
            case EXCHANGE:
                // 兑换券，value 为兑换金额上限
                discount = amount.min(userCoupon.getValue());
                break;
        }

        // 优惠金额不超过订单金额
        return discount.min(amount);
    }

    /**
     * 转换为可领取优惠券VO
     */
    private AvailableCouponVO convertToAvailableCouponVO(Coupon coupon, boolean received) {
        AvailableCouponVO vo = new AvailableCouponVO();
        vo.setId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setTypeName(CouponType.getByCode(coupon.getType()).getDesc());
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO);
        vo.setValidDays(coupon.getValidDays());
        vo.setValidStart(coupon.getValidStart());
        vo.setValidEnd(coupon.getValidEnd());
        vo.setTotalCount(coupon.getTotalCount());
        vo.setReceiveCount(coupon.getReceiveCount());
        vo.setLimitPerUser(coupon.getLimitPerUser());
        vo.setReceived(received);
        vo.setScope(coupon.getScope());
        vo.setScopeName(getScopeName(coupon.getScope()));
        vo.setDescription(buildDescription(coupon));
        return vo;
    }

    /**
     * 转换为用户优惠券VO
     */
    private UserCouponVO convertToUserCouponVO(UserCoupon userCoupon) {
        UserCouponVO vo = new UserCouponVO();
        vo.setId(userCoupon.getId());
        vo.setCouponId(userCoupon.getCouponId());
        vo.setName(userCoupon.getCouponName());
        vo.setType(userCoupon.getType());
        vo.setTypeName(CouponType.getByCode(userCoupon.getType()).getDesc());
        vo.setValue(userCoupon.getValue());
        vo.setMinAmount(userCoupon.getMinAmount());
        vo.setValidStart(userCoupon.getValidStart());
        vo.setValidEnd(userCoupon.getValidEnd());
        vo.setStatus(userCoupon.getStatus());
        vo.setStatusName(CouponStatus.getByCode(userCoupon.getStatus()).getDesc());
        vo.setUseTime(userCoupon.getUseTime());

        // 获取适用范围
        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon != null) {
            vo.setScope(coupon.getScope());
            vo.setScopeName(getScopeName(coupon.getScope()));
        }

        return vo;
    }

    /**
     * 转换为优惠券详情VO
     */
    private CouponDetailVO convertToCouponDetailVO(Coupon coupon) {
        CouponDetailVO vo = new CouponDetailVO();
        vo.setId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setTypeName(CouponType.getByCode(coupon.getType()).getDesc());
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO);
        vo.setTotalCount(coupon.getTotalCount());
        vo.setReceiveCount(coupon.getReceiveCount());
        vo.setUsedCount(coupon.getUsedCount());
        vo.setLimitPerUser(coupon.getLimitPerUser());
        vo.setValidDays(coupon.getValidDays());
        vo.setValidStart(coupon.getValidStart());
        vo.setValidEnd(coupon.getValidEnd());
        vo.setScope(coupon.getScope());
        vo.setScopeName(getScopeName(coupon.getScope()));
        vo.setScopeIds(coupon.getScopeIds());
        vo.setStatus(coupon.getStatus());
        vo.setDescription(buildDescription(coupon));
        vo.setCreateTime(coupon.getCreateTime());
        return vo;
    }

    /**
     * 转换为管理端优惠券VO
     */
    private AdminCouponVO convertToAdminCouponVO(Coupon coupon) {
        AdminCouponVO vo = new AdminCouponVO();
        vo.setId(coupon.getId());
        vo.setName(coupon.getName());
        vo.setType(coupon.getType());
        vo.setTypeName(CouponType.getByCode(coupon.getType()).getDesc());
        vo.setValue(coupon.getValue());
        vo.setMinAmount(coupon.getMinAmount() != null ? coupon.getMinAmount() : BigDecimal.ZERO);
        vo.setTotalCount(coupon.getTotalCount());
        vo.setReceiveCount(coupon.getReceiveCount());
        vo.setUsedCount(coupon.getUsedCount());
        vo.setLimitPerUser(coupon.getLimitPerUser());
        vo.setValidStart(coupon.getValidStart());
        vo.setValidEnd(coupon.getValidEnd());
        vo.setScope(coupon.getScope());
        vo.setScopeName(getScopeName(coupon.getScope()));
        vo.setStatus(coupon.getStatus());
        vo.setCreateTime(coupon.getCreateTime());
        return vo;
    }

    /**
     * 获取适用范围名称
     */
    private String getScopeName(Integer scope) {
        if (scope == null) {
            return "全部";
        }
        switch (scope) {
            case 1:
                return "全部电影";
            case 2:
                return "指定电影";
            case 3:
                return "指定影院";
            default:
                return "未知";
        }
    }

    /**
     * 构建优惠券描述
     */
    private String buildDescription(Coupon coupon) {
        StringBuilder sb = new StringBuilder();

        switch (CouponType.getByCode(coupon.getType())) {
            case FULL_REDUCTION:
                sb.append("满").append(coupon.getMinAmount()).append("元可用");
                break;
            case DISCOUNT:
                sb.append(coupon.getValue().multiply(BigDecimal.TEN).intValue()).append("折优惠");
                break;
            case INSTANT:
                sb.append("立减").append(coupon.getValue()).append("元");
                break;
            case EXCHANGE:
                sb.append("可兑换").append(coupon.getValue()).append("元内电影票");
                break;
        }

        sb.append("，").append(getScopeName(coupon.getScope())).append("可用");

        return sb.toString();
    }

    /**
     * 验证优惠券参数
     */
    private void validateCouponSaveDTO(CouponSaveDTO dto) {
        // 折扣券的值应该在 0-1 之间
        if (dto.getType() == CouponType.DISCOUNT.getCode()) {
            if (dto.getValue().compareTo(BigDecimal.ZERO) <= 0
                    || dto.getValue().compareTo(BigDecimal.ONE) >= 0) {
                throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "折扣值应在0-1之间");
            }
        }

        // 满减券需要设置最低消费金额
        if (dto.getType() == CouponType.FULL_REDUCTION.getCode() && dto.getMinAmount() == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "满减券需要设置最低消费金额");
        }

        // 指定范围时需要设置scopeIds
        if ((dto.getScope() == 2 || dto.getScope() == 3) && StrUtil.isBlank(dto.getScopeIds())) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "请指定适用范围");
        }
    }

}
