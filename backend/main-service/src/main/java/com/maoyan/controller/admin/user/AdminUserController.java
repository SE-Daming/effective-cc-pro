package com.maoyan.controller.admin.user;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.user.AdminUserQueryDTO;
import com.maoyan.dto.user.UpdateUserStatusRequest;
import com.maoyan.service.UserService;
import com.maoyan.vo.user.AdminUserDetailVO;
import com.maoyan.vo.user.AdminUserListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * B端用户管理控制器
 *
 * @author maoyan
 */
@Tag(name = "B端-用户管理", description = "用户管理接口")
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "用户列表")
    @GetMapping
    public Result<Map<String, Object>> getUserList(AdminUserQueryDTO queryDTO) {
        Page<AdminUserListVO> page = userService.getAdminUserList(queryDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("page", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.success(data);
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<AdminUserDetailVO> getUserDetail(
            @Parameter(description = "用户ID") @PathVariable Long id) {
        AdminUserDetailVO vo = userService.getAdminUserDetail(id);
        return Result.success(vo);
    }

    @Operation(summary = "禁用/启用用户")
    @PatchMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        userService.updateUserStatus(id, request);
        return Result.success();
    }

}
