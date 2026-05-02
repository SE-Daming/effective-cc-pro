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

/**
 * C端用户控制器
 *
 * @author maoyan
 */
@Tag(name = "C端-用户", description = "用户登录、个人信息、搜索历史等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "微信登录")
    @PostMapping("/wx-login")
    public Result<WxLoginVO> wxLogin(@Valid @RequestBody WxLoginRequest request) {
        WxLoginVO vo = userService.wxLogin(request);
        return Result.success(vo);
    }

}
