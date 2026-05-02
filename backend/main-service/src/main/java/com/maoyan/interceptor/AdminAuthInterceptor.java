package com.maoyan.interceptor;

import cn.hutool.core.util.StrUtil;
import com.maoyan.common.ResultCode;
import com.maoyan.exception.BusinessException;
import com.maoyan.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 管理员 JWT 认证拦截器
 *
 * @author maoyan
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_NAME = "Authorization";
    private static final String ADMIN_ID_ATTR = "adminId";
    private static final String USERNAME_ATTR = "username";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取 Token
        String authHeader = request.getHeader(HEADER_NAME);

        if (StrUtil.isBlank(authHeader)) {
            log.warn("请求头缺少 Authorization: uri={}", request.getRequestURI());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 验证 Token 格式
        if (!authHeader.startsWith(TOKEN_PREFIX)) {
            log.warn("Token 格式错误: uri={}", request.getRequestURI());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        String token = authHeader.substring(TOKEN_PREFIX.length());

        // 验证 Token 有效性
        if (!jwtUtil.validateToken(token)) {
            log.warn("Token 无效或已过期: uri={}", request.getRequestURI());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 从 Token 中获取管理员信息
        Integer adminId = jwtUtil.getAdminId(token);
        String username = jwtUtil.getUsername(token);

        if (adminId == null || StrUtil.isBlank(username)) {
            log.warn("Token 解析失败: uri={}", request.getRequestURI());
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        // 将管理员信息存入请求属性
        request.setAttribute(ADMIN_ID_ATTR, adminId);
        request.setAttribute(USERNAME_ATTR, username);

        return true;
    }

}
