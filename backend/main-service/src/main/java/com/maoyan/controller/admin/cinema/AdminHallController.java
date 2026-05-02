package com.maoyan.controller.admin.cinema;

import com.maoyan.common.Result;
import com.maoyan.dto.HallCreateDTO;
import com.maoyan.service.HallService;
import com.maoyan.vo.HallListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * B端影厅管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-影厅管理", description = "影厅管理接口")
@RestController
@RequestMapping("/api/admin/halls")
@RequiredArgsConstructor
public class AdminHallController {

    private final HallService hallService;

    @Operation(summary = "影厅列表")
    @GetMapping
    public Result<Map<String, Object>> getHallList(
            @Parameter(description = "影院ID") @RequestParam Long cinemaId) {
        List<HallListVO> list = hallService.getHallsByCinemaId(cinemaId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

    @Operation(summary = "新增影厅")
    @PostMapping
    public Result<Long> createHall(@Valid @RequestBody HallCreateDTO createDTO) {
        Long id = hallService.createHall(createDTO);
        return Result.success(id);
    }

    @Operation(summary = "更新影厅")
    @PutMapping("/{id}")
    public Result<Void> updateHall(
            @Parameter(description = "影厅ID") @PathVariable Long id,
            @Valid @RequestBody HallCreateDTO createDTO) {
        hallService.updateHall(id, createDTO);
        return Result.success();
    }

    @Operation(summary = "删除影厅")
    @DeleteMapping("/{id}")
    public Result<Void> deleteHall(
            @Parameter(description = "影厅ID") @PathVariable Long id) {
        hallService.deleteHall(id);
        return Result.success();
    }

}
