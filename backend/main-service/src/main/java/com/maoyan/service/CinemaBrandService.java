package com.maoyan.service;

import com.maoyan.vo.CinemaBrandVO;

import java.util.List;

/**
 * 影院品牌服务接口
 *
 * @author maoyan
 */
public interface CinemaBrandService {

    /**
     * 获取所有影院品牌
     */
    List<CinemaBrandVO> getAllBrands();

}
