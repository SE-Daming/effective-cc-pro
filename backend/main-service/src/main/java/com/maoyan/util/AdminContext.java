package com.maoyan.util;

import com.maoyan.common.ResultCode;
import com.maoyan.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 管理员上下文工具类
 *
 * @author maoyan
 */
public class AdminContext {

    private static final String ADMIN_ID_ATTR = "adminId";
    private static final String USERNAME_ATTR = "username";

    private AdminContext() {
    }

    /**
     * 获取当前请求
     */
    private static HttpServletRequest getRequest() {
        ServletRequestAttributes attributes =
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前管理员ID
     */
    public static Integer getAdminId() {
        Object adminId = getRequest().getAttribute(ADMIN_ID_ATTR);
        if (adminId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return (Integer) adminId;
    }

    /**
     * 获取当前管理员用户名
     */
    public static String getUsername() {
        Object username = getRequest().getAttribute(USERNAME_ATTR);
        if (username == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return (String) username;
    }

}
