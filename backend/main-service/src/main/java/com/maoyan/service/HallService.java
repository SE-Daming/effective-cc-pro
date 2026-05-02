package com.maoyan.service;

import com.maoyan.dto.HallCreateDTO;
import com.maoyan.entity.Hall;
import com.maoyan.vo.HallListVO;

import java.util.List;

/**
 * 影厅服务接口
 *
 * @author maoyan
 */
public interface HallService {

    /**
     * 获取影院的影厅列表
     */
    List<HallListVO> getHallsByCinemaId(Long cinemaId);

    /**
     * 创建影厅（自动生成座位）
     */
    Long createHall(HallCreateDTO createDTO);

    /**
     * 更新影厅
     */
    void updateHall(Long id, HallCreateDTO createDTO);

    /**
     * 删除影厅
     */
    void deleteHall(Long id);

    /**
     * 根据ID获取影厅实体
     */
    Hall getById(Long id);

}
