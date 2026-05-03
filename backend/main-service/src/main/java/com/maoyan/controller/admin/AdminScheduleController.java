package com.maoyan.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.ScheduleBatchCreateRequest;
import com.maoyan.dto.ScheduleConflictCheckRequest;
import com.maoyan.dto.ScheduleCreateRequest;
import com.maoyan.service.ScheduleService;
import com.maoyan.vo.ScheduleConflictCheckVO;
import com.maoyan.vo.ScheduleListItemVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * B端排片管理控制器
 *
 * @author maoyan
 */
@Tag(name = "B端-排片管理", description = "排片CRUD、冲突检测相关接口")
@RestController
@RequestMapping("/admin/schedules")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 获取排片列表
     */
    @Operation(summary = "获取排片列表", description = "分页查询排片列表")
    @GetMapping
    public Result<Page<ScheduleListItemVO>> getScheduleList(
            @Parameter(description = "影院ID") @RequestParam(required = false) Long cinemaId,
            @Parameter(description = "电影ID") @RequestParam(required = false) Long movieId,
            @Parameter(description = "放映日期") @RequestParam(required = false) String showDate,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(scheduleService.getScheduleList(cinemaId, movieId, showDate, page, pageSize));
    }

    /**
     * 新增排片
     */
    @Operation(summary = "新增排片", description = "创建单场排片，自动检测冲突")
    @PostMapping
    public Result<Long> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        return Result.success(scheduleService.createSchedule(request));
    }

    /**
     * 批量排片
     */
    @Operation(summary = "批量排片", description = "批量创建多天多场次的排片")
    @PostMapping("/batch")
    public Result<Integer> batchCreateSchedule(@Valid @RequestBody ScheduleBatchCreateRequest request) {
        return Result.success(scheduleService.batchCreateSchedule(request));
    }

    /**
     * 删除排片
     */
    @Operation(summary = "删除排片", description = "删除场次，已售票的场次无法删除")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSchedule(
            @Parameter(description = "场次ID") @PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return Result.success();
    }

    /**
     * 排片冲突检测
     */
    @Operation(summary = "排片冲突检测", description = "检测指定时间是否与现有排片冲突")
    @PostMapping("/check-conflict")
    public Result<ScheduleConflictCheckVO> checkConflict(@RequestBody ScheduleConflictCheckRequest request) {
        return Result.success(scheduleService.checkConflict(request));
    }

    /**
     * 获取排片日历数据
     */
    @Operation(summary = "获取排片日历数据", description = "获取指定日期范围内的排片日历数据")
    @GetMapping("/calendar")
    public Result<Map<String, List<ScheduleListItemVO>>> getScheduleCalendar(
            @Parameter(description = "开始日期") @RequestParam String startDate,
            @Parameter(description = "结束日期") @RequestParam String endDate) {
        return Result.success(scheduleService.getScheduleCalendar(startDate, endDate));
    }

}
