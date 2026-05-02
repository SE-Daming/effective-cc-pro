package com.maoyan.controller.api.cinema;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.CinemaQueryDTO;
import com.maoyan.dto.NearbyCinemaQueryDTO;
import com.maoyan.service.CinemaService;
import com.maoyan.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * C端影院接口
 *
 * @author maoyan
 */
@Tag(name = "C端-影院", description = "影院相关接口")
@RestController
@RequestMapping("/api/cinemas")
@RequiredArgsConstructor
public class CinemaController {

    private final CinemaService cinemaService;

    @Operation(summary = "影院列表")
    @GetMapping
    public Result<Map<String, Object>> getCinemaList(CinemaQueryDTO queryDTO) {
        Page<CinemaListVO> page = cinemaService.getCinemaList(queryDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", page.getRecords());
        data.put("total", page.getTotal());
        data.put("page", page.getCurrent());
        data.put("pageSize", page.getSize());
        return Result.success(data);
    }

    @Operation(summary = "附近影院")
    @GetMapping("/nearby")
    public Result<Map<String, Object>> getNearbyCinemas(NearbyCinemaQueryDTO queryDTO) {
        List<CinemaListVO> list = cinemaService.getNearbyCinemas(queryDTO);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

    @Operation(summary = "影院详情")
    @GetMapping("/{id}")
    public Result<CinemaDetailVO> getCinemaDetail(
            @Parameter(description = "影院ID") @PathVariable Long id) {
        return Result.success(cinemaService.getCinemaDetail(id));
    }

    @Operation(summary = "影院影厅列表")
    @GetMapping("/{id}/halls")
    public Result<Map<String, Object>> getCinemaHalls(
            @Parameter(description = "影院ID") @PathVariable Long id) {
        List<HallListVO> list = cinemaService.getCinemaHalls(id);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

    @Operation(summary = "影院排片日期")
    @GetMapping("/{id}/schedule-dates")
    public Result<Map<String, Object>> getScheduleDates(
            @Parameter(description = "影院ID") @PathVariable Long id,
            @Parameter(description = "电影ID") @RequestParam(required = false) Long movieId) {
        List<ScheduleDateVO> list = cinemaService.getScheduleDates(id, movieId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

    @Operation(summary = "影院某日排片")
    @GetMapping("/{id}/schedules")
    public Result<Map<String, Object>> getCinemaSchedules(
            @Parameter(description = "影院ID") @PathVariable Long id,
            @Parameter(description = "放映日期") @RequestParam String date,
            @Parameter(description = "电影ID") @RequestParam(required = false) Long movieId) {
        List<CinemaScheduleVO> list = cinemaService.getCinemaSchedules(id, date, movieId);
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);
        return Result.success(data);
    }

}
