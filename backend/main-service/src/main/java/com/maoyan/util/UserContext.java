package com.maoyan.util;

import com.maoyan.common.ResultCode;
import com.maoyan.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 用户上下文工具类
 *
 * @author maoyan
 */
public class UserContext {

    private static final String USER_ID_ATTR = "userId";

    private UserContext() {
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
     * 获取当前用户ID
     */
    public static Long getUserId() {
        Object userId = getRequest().getAttribute(USER_ID_ATTR);
        if (userId == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return (Long) userId;
    }

}
