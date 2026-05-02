package com.maoyan.controller.admin;

import com.maoyan.common.Result;
import com.maoyan.dto.admin.AdminInfoResponse;
import com.maoyan.dto.admin.AdminLoginRequest;
import com.maoyan.dto.admin.AdminLoginResponse;
import com.maoyan.service.AdminAuthService;
import com.maoyan.util.AdminContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理员认证控制器
 *
 * @author maoyan
 */
@Tag(name = "管理员认证", description = "管理员登录、登出、获取信息等接口")
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    /**
     * 管理员登录
     */
    @Operation(summary = "管理员登录")
    @PostMapping("/login")
    public Result<AdminLoginResponse> login(@Valid @RequestBody AdminLoginRequest request,
                                            HttpServletRequest httpRequest) {
        String clientIp = getClientIp(httpRequest);
        AdminLoginResponse response = adminAuthService.login(request, clientIp);
        return Result.success(response);
    }

    /**
     * 获取当前管理员信息
     */
    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/info")
    public Result<AdminInfoResponse> getAdminInfo() {
        Integer adminId = AdminContext.getAdminId();
        AdminInfoResponse response = adminAuthService.getAdminInfo(adminId);
        return Result.success(response);
    }

    /**
     * 管理员登出
     */
    @Operation(summary = "管理员登出")
    @PostMapping("/logout")
    public Result<Void> logout() {
        Integer adminId = AdminContext.getAdminId();
        adminAuthService.logout(adminId);
        return Result.success();
    }

    /**
     * 获取客户端 IP
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

}
