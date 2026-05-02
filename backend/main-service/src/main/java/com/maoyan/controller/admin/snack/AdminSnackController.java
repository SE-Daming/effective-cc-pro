package com.maoyan.controller.admin.snack;

import com.maoyan.common.Result;
import com.maoyan.dto.snack.SnackQueryDTO;
import com.maoyan.dto.snack.SnackSaveDTO;
import com.maoyan.service.SnackService;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.snack.SnackListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * B端 - 卖品管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-卖品管理", description = "卖品CRUD接口")
@RestController
@RequestMapping("/admin/snacks")
@RequiredArgsConstructor
public class AdminSnackController {

    private final SnackService snackService;

    @Operation(summary = "获取卖品列表")
    @GetMapping
    public Result<PageVO<SnackListVO>> listSnacks(@Valid SnackQueryDTO queryDTO) {
        return Result.success(snackService.getSnackListAdmin(queryDTO));
    }

    @Operation(summary = "新增卖品")
    @PostMapping
    public Result<Long> createSnack(@Valid @RequestBody SnackSaveDTO dto) {
        return Result.success(snackService.createSnack(dto));
    }

    @Operation(summary = "更新卖品")
    @PutMapping("/{id}")
    public Result<Void> updateSnack(
            @Parameter(description = "卖品ID") @PathVariable Long id,
            @Valid @RequestBody SnackSaveDTO dto) {
        snackService.updateSnack(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除卖品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSnack(
            @Parameter(description = "卖品ID") @PathVariable Long id) {
        snackService.deleteSnack(id);
        return Result.success();
    }
}
