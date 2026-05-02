package com.maoyan.controller.admin;

import com.maoyan.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台管理 - 健康检查接口
 *
 * @author maoyan
 */
@Tag(name = "后台管理 - 健康检查", description = "后台管理健康检查接口")
@RestController
@RequestMapping("/admin/health")
public class AdminHealthController {

    @Operation(summary = "健康检查")
    @GetMapping
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "maoyan-main-service-admin");
        data.put("timestamp", LocalDateTime.now());
        return Result.success(data);
    }

}
