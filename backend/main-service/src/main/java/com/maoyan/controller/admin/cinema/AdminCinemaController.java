package com.maoyan.controller.admin.cinema;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.AdminCinemaQueryDTO;
import com.maoyan.dto.CinemaCreateDTO;
import com.maoyan.service.CinemaService;
import com.maoyan.vo.CinemaListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * B端影院管理接口
 *
 * @author maoyan
 */
@Tag(name = "B端-影院管理", description = "影院管理接口")
@RestController
@RequestMapping("/api/admin/cinemas")
@RequiredArgsConstructor
public class AdminCinemaController {

    private final CinemaService cinemaService;

    @Operation(summary = "影院列表")
    @GetMapping
    public Result<Map<String, Object>> getCinemaList(AdminCinemaQueryDTO queryDTO) {
        Page<CinemaListVO> page = cinemaService.getAdminCinemaList(queryDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("page", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.success(data);
    }

    @Operation(summary = "新增影院")
    @PostMapping
    public Result<Long> createCinema(@Valid @RequestBody CinemaCreateDTO createDTO) {
        Long id = cinemaService.createCinema(createDTO);
        return Result.success(id);
    }

    @Operation(summary = "更新影院")
    @PutMapping("/{id}")
    public Result<Void> updateCinema(
            @Parameter(description = "影院ID") @PathVariable Long id,
            @Valid @RequestBody CinemaCreateDTO createDTO) {
        cinemaService.updateCinema(id, createDTO);
        return Result.success();
    }

    @Operation(summary = "删除影院")
    @DeleteMapping("/{id}")
    public Result<Void> deleteCinema(
            @Parameter(description = "影院ID") @PathVariable Long id) {
        cinemaService.deleteCinema(id);
        return Result.success();
    }

}
