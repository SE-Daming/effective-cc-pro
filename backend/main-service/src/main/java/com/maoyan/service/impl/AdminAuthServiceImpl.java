package com.maoyan.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.admin.AdminInfoResponse;
import com.maoyan.dto.admin.AdminLoginRequest;
import com.maoyan.dto.admin.AdminLoginResponse;
import com.maoyan.entity.Admin;
import com.maoyan.entity.AdminRole;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.AdminMapper;
import com.maoyan.mapper.AdminRoleMapper;
import com.maoyan.service.AdminAuthService;
import com.maoyan.util.JwtUtil;
import com.maoyan.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 管理员认证服务实现
 *
 * @author maoyan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminMapper adminMapper;
    private final AdminRoleMapper adminRoleMapper;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AdminLoginResponse login(AdminLoginRequest request, String clientIp) {
        // 1. 查询管理员
        Admin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<Admin>()
                        .eq(Admin::getUsername, request.getUsername())
        );

        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }

        // 2. 验证密码
        if (!PasswordUtil.matches(request.getPassword(), admin.getPassword())) {
            throw new BusinessException(ResultCode.ADMIN_PASSWORD_ERROR);
        }

        // 3. 检查状态
        if (admin.getStatus() == null || admin.getStatus() != 1) {
            throw new BusinessException(ResultCode.ADMIN_DISABLED);
        }

        // 4. 更新最后登录时间和IP
        Admin updateAdmin = new Admin();
        updateAdmin.setId(admin.getId());
        updateAdmin.setLastLoginTime(LocalDateTime.now());
        updateAdmin.setLastLoginIp(clientIp);
        adminMapper.updateById(updateAdmin);

        // 5. 生成 Token
        String token = jwtUtil.generateToken(admin.getId(), admin.getUsername());

        // 6. 构建响应
        AdminLoginResponse response = new AdminLoginResponse();
        response.setToken(token);
        response.setAdminInfo(buildAdminInfo(admin));

        log.info("管理员登录成功: username={}, ip={}", admin.getUsername(), clientIp);

        return response;
    }

    @Override
    public AdminInfoResponse getAdminInfo(Integer adminId) {
        Admin admin = adminMapper.selectById(adminId);
        if (admin == null) {
            throw new BusinessException(ResultCode.ADMIN_NOT_FOUND);
        }
        return buildAdminInfo(admin);
    }

    @Override
    public void logout(Integer adminId) {
        log.info("管理员登出: adminId={}", adminId);
        // JWT 无状态，服务端不需要处理
        // 如果需要实现 Token 黑名单，可以在这里添加逻辑
    }

    /**
     * 构建管理员信息响应
     */
    private AdminInfoResponse buildAdminInfo(Admin admin) {
        AdminInfoResponse info = new AdminInfoResponse();
        info.setId(admin.getId());
        info.setUsername(admin.getUsername());
        info.setRealName(admin.getRealName());
        info.setPhone(maskPhone(admin.getPhone()));
        info.setEmail(admin.getEmail());
        info.setRoleId(admin.getRoleId());
        info.setLastLoginTime(admin.getLastLoginTime());

        // 查询角色信息
        if (admin.getRoleId() != null) {
            AdminRole role = adminRoleMapper.selectById(admin.getRoleId());
            if (role != null) {
                info.setRoleName(role.getName());
                // 解析权限 JSON
                if (StrUtil.isNotBlank(role.getPermissions())) {
                    info.setPermissions(JSONUtil.parseObj(role.getPermissions()));
                } else {
                    info.setPermissions(null);
                }
            }
        }

        return info;
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

}
