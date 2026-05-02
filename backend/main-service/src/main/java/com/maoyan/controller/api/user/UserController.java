package com.maoyan.controller.api.user;

import com.maoyan.common.Result;
import com.maoyan.dto.user.*;
import com.maoyan.service.UserService;
import com.maoyan.util.UserContext;
import com.maoyan.vo.user.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * C端用户控制器
 *
 * @author maoyan
 */
@Tag(name = "C端-用户", description = "用户登录、个人信息、搜索历史等接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "更新用户信息")
    @PutMapping("/profile")
    public Result<UserInfoVO> updateProfile(@Valid @RequestBody UserProfileUpdateRequest request) {
        Long userId = UserContext.getUserId();
        UserInfoVO vo = userService.updateProfile(userId, request);
        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserInfoVO> getUserInfo() {
        Long userId = UserContext.getUserId();
        UserInfoVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    @Operation(summary = "绑定手机号")
    @PostMapping("/bind-phone")
    public Result<Void> bindPhone(@Valid @RequestBody BindPhoneRequest request) {
        Long userId = UserContext.getUserId();
        userService.bindPhone(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取搜索历史")
    @GetMapping("/search-history")
    public Result<SearchHistoryListVO> getSearchHistory(SearchHistoryQueryDTO queryDTO) {
        Long userId = UserContext.getUserId();
        SearchHistoryListVO vo = userService.getSearchHistory(userId, queryDTO);
        return Result.success(vo);
    }

    @Operation(summary = "清空搜索历史")
    @DeleteMapping("/search-history")
    public Result<Void> clearSearchHistory(@RequestBody(required = false) ClearSearchHistoryRequest request) {
        Long userId = UserContext.getUserId();
        if (request == null) {
            request = new ClearSearchHistoryRequest();
        }
        userService.clearSearchHistory(userId, request);
        return Result.success();
    }

    @Operation(summary = "获取用户统计数据")
    @GetMapping("/statistics")
    public Result<UserStatisticsVO> getUserStatistics() {
        Long userId = UserContext.getUserId();
        UserStatisticsVO vo = userService.getUserStatistics(userId);
        return Result.success(vo);
    }

}
