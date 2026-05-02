package com.maoyan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.user.*;
import com.maoyan.entity.SearchHistory;
import com.maoyan.entity.User;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.SearchHistoryMapper;
import com.maoyan.mapper.UserMapper;
import com.maoyan.service.UserService;
import com.maoyan.util.JwtUtil;
import com.maoyan.vo.user.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final SearchHistoryMapper searchHistoryMapper;
    private final JwtUtil jwtUtil;

    // ==================== C端接口 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WxLoginVO wxLogin(WxLoginRequest request) {
        // 模拟微信登录：实际项目中需要调用微信API获取openid
        // 这里简化处理，使用code模拟openid
        String openid = "wx_" + request.getCode();

        // 查询用户是否存在
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
        );

        boolean isNewUser = false;

        if (user == null) {
            // 新用户注册
            isNewUser = true;
            user = new User();
            user.setOpenid(openid);
            user.setNickname("用户" + openid.substring(openid.length() - 6));
            user.setAvatar(null);
            user.setGender(0);
            user.setTotalConsumption(BigDecimal.ZERO);
            user.setStatus(1);
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.insert(user);
            log.info("新用户注册: userId={}, openid={}", user.getId(), openid);
        } else {
            // 检查用户状态
            if (user.getStatus() == null || user.getStatus() != 1) {
                throw new BusinessException(ResultCode.USER_DISABLED);
            }
            // 更新最后登录时间
            User updateUser = new User();
            updateUser.setId(user.getId());
            updateUser.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(updateUser);
        }

        // 生成Token
        String token = jwtUtil.generateUserToken(user.getId());

        // 构建响应
        WxLoginVO vo = new WxLoginVO();
        vo.setToken(token);
        vo.setIsNewUser(isNewUser);
        vo.setUserInfo(buildUserInfo(user));

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserInfoVO updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 更新用户信息
        User updateUser = new User();
        updateUser.setId(userId);
        if (StrUtil.isNotBlank(request.getNickname())) {
            updateUser.setNickname(request.getNickname());
        }
        if (StrUtil.isNotBlank(request.getAvatar())) {
            updateUser.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            updateUser.setGender(request.getGender());
        }
        userMapper.updateById(updateUser);

        // 返回更新后的用户信息
        user = userMapper.selectById(userId);
        return buildUserInfo(user);
    }

    @Override
    public UserInfoVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return buildUserInfo(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindPhone(Long userId, BindPhoneRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 检查手机号是否已被绑定
        User existUser = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getPhone, request.getPhone())
                        .ne(User::getId, userId)
        );
        if (existUser != null) {
            throw new BusinessException(ResultCode.USER_PHONE_EXISTS);
        }

        // TODO: 验证验证码

        // 绑定手机号
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPhone(request.getPhone());
        userMapper.updateById(updateUser);

        log.info("用户绑定手机号: userId={}, phone={}", userId, maskPhone(request.getPhone()));
    }

    @Override
    public SearchHistoryListVO getSearchHistory(Long userId, SearchHistoryQueryDTO queryDTO) {
        LambdaQueryWrapper<SearchHistory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        if (queryDTO.getType() != null) {
            wrapper.eq(SearchHistory::getType, queryDTO.getType());
        }
        wrapper.orderByDesc(SearchHistory::getCreateTime);
        wrapper.last("LIMIT " + queryDTO.getLimit());

        List<SearchHistory> list = searchHistoryMapper.selectList(wrapper);

        List<SearchHistoryVO> voList = list.stream().map(this::buildSearchHistoryVO).collect(Collectors.toList());

        SearchHistoryListVO vo = new SearchHistoryListVO();
        vo.setList(voList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearSearchHistory(Long userId, ClearSearchHistoryRequest request) {
        LambdaUpdateWrapper<SearchHistory> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(SearchHistory::getUserId, userId);
        if (request.getType() != null) {
            wrapper.eq(SearchHistory::getType, request.getType());
        }
        searchHistoryMapper.delete(wrapper);
    }

    @Override
    public UserStatisticsVO getUserStatistics(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserStatisticsVO vo = new UserStatisticsVO();
        vo.setTotalConsumption(user.getTotalConsumption());

        // TODO: 统计订单数量、优惠券数量、收藏数量
        // 目前返回模拟数据
        vo.setOrderCount(0);
        vo.setCouponCount(0);
        vo.setFavoriteCount(0);

        return vo;
    }

    // ==================== 管理端接口 ====================

    @Override
    public Page<AdminUserListVO> getAdminUserList(AdminUserQueryDTO queryDTO) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (StrUtil.isNotBlank(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(User::getNickname, queryDTO.getKeyword())
                    .or()
                    .like(User::getPhone, queryDTO.getKeyword())
            );
        }

        // 状态筛选
        if (queryDTO.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDTO.getStatus());
        }

        wrapper.orderByDesc(User::getCreateTime);

        Page<User> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        Page<User> userPage = userMapper.selectPage(page, wrapper);

        // 转换为VO
        Page<AdminUserListVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<AdminUserListVO> voList = userPage.getRecords().stream()
                .map(this::buildAdminUserListVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);

        return voPage;
    }

    @Override
    public AdminUserDetailVO getAdminUserDetail(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        AdminUserDetailVO vo = new AdminUserDetailVO();
        BeanUtils.copyProperties(user, vo);
        vo.setOpenid(maskOpenid(user.getOpenid()));

        // TODO: 统计订单数量、优惠券数量、收藏数量
        vo.setOrderCount(0);
        vo.setCouponCount(0);
        vo.setFavoriteCount(0);

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(Long id, UpdateUserStatusRequest request) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        User updateUser = new User();
        updateUser.setId(id);
        updateUser.setStatus(request.getStatus());
        userMapper.updateById(updateUser);

        log.info("管理员更新用户状态: userId={}, status={}", id, request.getStatus());
    }

    // ==================== 私有方法 ====================

    private UserInfoVO buildUserInfo(User user) {
        UserInfoVO vo = new UserInfoVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setPhone(maskPhone(user.getPhone()));
        vo.setGender(user.getGender());
        vo.setTotalConsumption(user.getTotalConsumption());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    private SearchHistoryVO buildSearchHistoryVO(SearchHistory history) {
        SearchHistoryVO vo = new SearchHistoryVO();
        BeanUtils.copyProperties(history, vo);
        return vo;
    }

    private AdminUserListVO buildAdminUserListVO(User user) {
        AdminUserListVO vo = new AdminUserListVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (StrUtil.isBlank(phone) || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }

    /**
     * OpenID脱敏
     */
    private String maskOpenid(String openid) {
        if (StrUtil.isBlank(openid) || openid.length() < 10) {
            return openid;
        }
        return openid.substring(0, 6) + "****" + openid.substring(openid.length() - 4);
    }

}
