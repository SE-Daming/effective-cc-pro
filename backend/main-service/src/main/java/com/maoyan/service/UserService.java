package com.maoyan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.user.*;
import com.maoyan.vo.user.*;

/**
 * 用户服务接口
 *
 * @author maoyan
 */
public interface UserService {

    /**
     * 微信登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    WxLoginVO wxLogin(WxLoginRequest request);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 用户信息
     */
    UserInfoVO updateProfile(Long userId, UserProfileUpdateRequest request);

    /**
     * 获取当前用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    UserInfoVO getUserInfo(Long userId);

    /**
     * 绑定手机号
     *
     * @param userId  用户ID
     * @param request 绑定请求
     */
    void bindPhone(Long userId, BindPhoneRequest request);

    /**
     * 获取搜索历史
     *
     * @param userId  用户ID
     * @param queryDTO 查询参数
     * @return 搜索历史列表
     */
    SearchHistoryListVO getSearchHistory(Long userId, SearchHistoryQueryDTO queryDTO);

    /**
     * 清空搜索历史
     *
     * @param userId  用户ID
     * @param request 清空请求
     */
    void clearSearchHistory(Long userId, ClearSearchHistoryRequest request);

    /**
     * 获取用户统计数据
     *
     * @param userId 用户ID
     * @return 统计数据
     */
    UserStatisticsVO getUserStatistics(Long userId);

    // ==================== 管理端接口 ====================

    /**
     * 获取用户列表（管理端）
     *
     * @param queryDTO 查询参数
     * @return 分页列表
     */
    Page<AdminUserListVO> getAdminUserList(AdminUserQueryDTO queryDTO);

    /**
     * 获取用户详情（管理端）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    AdminUserDetailVO getAdminUserDetail(Long id);

    /**
     * 更新用户状态（管理端）
     *
     * @param id      用户ID
     * @param request 状态请求
     */
    void updateUserStatus(Long id, UpdateUserStatusRequest request);

}
