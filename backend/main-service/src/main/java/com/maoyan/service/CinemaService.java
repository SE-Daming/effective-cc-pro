package com.maoyan.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.maoyan.dto.*;
import com.maoyan.entity.Cinema;
import com.maoyan.vo.*;

import java.util.List;

/**
 * 影院服务接口
 *
 * @author maoyan
 */
public interface CinemaService {

    /**
     * 分页查询影院列表
     */
    Page<CinemaListVO> getCinemaList(CinemaQueryDTO queryDTO);

    /**
     * 获取附近影院
     */
    List<CinemaListVO> getNearbyCinemas(NearbyCinemaQueryDTO queryDTO);

    /**
     * 获取影院详情
     */
    CinemaDetailVO getCinemaDetail(Long id);

    /**
     * 获取影院影厅列表
     */
    List<HallListVO> getCinemaHalls(Long cinemaId);

    /**
     * 获取影院排片日期
     */
    List<ScheduleDateVO> getScheduleDates(Long cinemaId, Long movieId);

    /**
     * 获取影院某日排片
     */
    List<CinemaScheduleVO> getCinemaSchedules(Long cinemaId, String date, Long movieId);

    // ========== 管理端方法 ==========

    /**
     * 管理端分页查询影院
     */
    Page<CinemaListVO> getAdminCinemaList(AdminCinemaQueryDTO queryDTO);

    /**
     * 创建影院
     */
    Long createCinema(CinemaCreateDTO createDTO);

    /**
     * 更新影院
     */
    void updateCinema(Long id, CinemaCreateDTO createDTO);

    /**
     * 删除影院
     */
    void deleteCinema(Long id);

    /**
     * 根据ID获取影院实体
     */
    Cinema getById(Long id);

}
