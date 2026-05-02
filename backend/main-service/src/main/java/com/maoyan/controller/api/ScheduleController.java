package com.maoyan.controller.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.common.Result;
import com.maoyan.dto.LockSeatsRequest;
import com.maoyan.service.ScheduleService;
import com.maoyan.vo.LockSeatsVO;
import com.maoyan.vo.MovieCinemaScheduleVO;
import com.maoyan.vo.ScheduleDetailVO;
import com.maoyan.vo.ScheduleSeatVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端排片控制器
 *
 * @author maoyan
 */
@Tag(name = "C端-排片接口", description = "排片查询、座位选择相关接口")
@RestController
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 获取电影排片影院列表
     */
    @Operation(summary = "获取电影排片影院列表", description = "查询某电影在哪些影院有排片")
    @GetMapping("/movies/{id}/cinemas")
    public Result<Page<MovieCinemaScheduleVO>> getMovieCinemaSchedules(
            @Parameter(description = "电影ID") @PathVariable Long id,
            @Parameter(description = "城市") @RequestParam String city,
            @Parameter(description = "放映日期") @RequestParam String date,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(scheduleService.getMovieCinemaSchedules(id, city, date, page, pageSize));
    }

    /**
     * 获取排片详情
     */
    @Operation(summary = "获取排片详情", description = "获取场次详细信息")
    @GetMapping("/schedules/{id}")
    public Result<ScheduleDetailVO> getScheduleDetail(
            @Parameter(description = "场次ID") @PathVariable Long id) {
        return Result.success(scheduleService.getScheduleDetail(id));
    }

    /**
     * 获取场次座位图
     */
    @Operation(summary = "获取场次座位图", description = "获取场次的座位布局和状态")
    @GetMapping("/schedules/{id}/seats")
    public Result<ScheduleSeatVO> getScheduleSeats(
            @Parameter(description = "场次ID") @PathVariable Long id) {
        return Result.success(scheduleService.getScheduleSeats(id));
    }

    /**
     * 锁定座位
     */
    @Operation(summary = "锁定座位", description = "选座后锁定座位5分钟")
    @PostMapping("/schedules/{id}/lock-seats")
    public Result<LockSeatsVO> lockSeats(
            @Parameter(description = "场次ID") @PathVariable Long id,
            @Valid @RequestBody LockSeatsRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        return Result.success(scheduleService.lockSeats(id, request, userId));
    }

    /**
     * 释放座位锁
     */
    @Operation(summary = "释放座位锁", description = "取消选座时释放锁定的座位")
    @DeleteMapping("/seat-locks/{lockId}")
    public Result<Void> releaseSeatLock(
            @Parameter(description = "锁定ID") @PathVariable String lockId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // TODO: 从JWT中获取用户ID，暂时从请求头获取
        if (userId == null) {
            userId = 1L; // 临时测试用
        }
        scheduleService.releaseSeatLock(lockId, userId);
        return Result.success();
    }

}
