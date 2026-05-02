package com.maoyan.config;

import com.maoyan.interceptor.AdminAuthInterceptor;
import com.maoyan.interceptor.UserAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 配置
 *
 * @author maoyan
 */
@org.springframework.context.annotation.Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AdminAuthInterceptor adminAuthInterceptor;
    private final UserAuthInterceptor userAuthInterceptor;

    /**
     * 管理端接口不需要认证的路径
     */
    private static final List<String> ADMIN_EXCLUDE_PATHS = Arrays.asList(
            "/admin/auth/login",
            "/admin/auth/captcha",
            "/admin/health",
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
    );

    /**
     * 用户端接口不需要认证的路径
     */
    private static final List<String> USER_EXCLUDE_PATHS = Arrays.asList(
            "/auth/wx-login",
            "/health",
            "/doc.html",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**"
    );

    /**
     * 添加拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 管理端认证拦截器
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns(ADMIN_EXCLUDE_PATHS);

        // 用户端认证拦截器
        registry.addInterceptor(userAuthInterceptor)
                .addPathPatterns("/users/**")
                .excludePathPatterns(USER_EXCLUDE_PATHS);
    }

    /**
     * 跨域配置
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

}
