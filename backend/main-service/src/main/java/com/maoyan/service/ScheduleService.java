package com.maoyan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.LockSeatsRequest;
import com.maoyan.dto.ScheduleBatchCreateRequest;
import com.maoyan.dto.ScheduleConflictCheckRequest;
import com.maoyan.dto.ScheduleCreateRequest;
import com.maoyan.vo.*;

/**
 * 排片服务接口
 *
 * @author maoyan
 */
public interface ScheduleService {

    /**
     * 获取电影排片影院列表（C端）
     *
     * @param movieId  电影ID
     * @param city     城市
     * @param date     放映日期
     * @param page     页码
     * @param pageSize 每页数量
     * @return 影院排片列表
     */
    Page<MovieCinemaScheduleVO> getMovieCinemaSchedules(Long movieId, String city, String date, Integer page, Integer pageSize);

    /**
     * 获取排片详情（C端）
     *
     * @param id 场次ID
     * @return 排片详情
     */
    ScheduleDetailVO getScheduleDetail(Long id);

    /**
     * 获取场次座位图（C端）
     *
     * @param scheduleId 场次ID
     * @return 座位图
     */
    ScheduleSeatVO getScheduleSeats(Long scheduleId);

    /**
     * 锁定座位（C端）
     *
     * @param scheduleId 场次ID
     * @param request    锁定请求
     * @param userId     用户ID
     * @return 锁定结果
     */
    LockSeatsVO lockSeats(Long scheduleId, LockSeatsRequest request, Long userId);

    /**
     * 释放座位锁（C端）
     *
     * @param lockId 锁定ID
     * @param userId 用户ID
     */
    void releaseSeatLock(String lockId, Long userId);

    /**
     * 获取排片列表（B端）
     *
     * @param cinemaId  影院ID
     * @param movieId   电影ID
     * @param showDate  放映日期
     * @param page      页码
     * @param pageSize  每页数量
     * @return 排片列表
     */
    Page<ScheduleListItemVO> getScheduleList(Long cinemaId, Long movieId, String showDate, Integer page, Integer pageSize);

    /**
     * 新增排片（B端）
     *
     * @param request 排片请求
     * @return 场次ID
     */
    Long createSchedule(ScheduleCreateRequest request);

    /**
     * 批量排片（B端）
     *
     * @param request 批量排片请求
     * @return 创建的场次数量
     */
    Integer batchCreateSchedule(ScheduleBatchCreateRequest request);

    /**
     * 删除排片（B端）
     *
     * @param id 场次ID
     */
    void deleteSchedule(Long id);

    /**
     * 检测排片冲突（B端）
     *
     * @param request 冲突检测请求
     * @return 冲突检测结果
     */
    ScheduleConflictCheckVO checkConflict(ScheduleConflictCheckRequest request);

}
