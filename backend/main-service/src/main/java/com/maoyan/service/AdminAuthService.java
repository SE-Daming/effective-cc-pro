package com.maoyan.service;

import com.maoyan.dto.admin.AdminInfoResponse;
import com.maoyan.dto.admin.AdminLoginRequest;
import com.maoyan.dto.admin.AdminLoginResponse;

/**
 * 管理员认证服务接口
 *
 * @author maoyan
 */
public interface AdminAuthService {

    /**
     * 管理员登录
     *
     * @param request 登录请求
     * @param clientIp 客户端IP
     * @return 登录响应
     */
    AdminLoginResponse login(AdminLoginRequest request, String clientIp);

    /**
     * 获取当前管理员信息
     *
     * @param adminId 管理员ID
     * @return 管理员信息
     */
    AdminInfoResponse getAdminInfo(Integer adminId);

    /**
     * 管理员登出
     *
     * @param adminId 管理员ID
     */
    void logout(Integer adminId);

}
