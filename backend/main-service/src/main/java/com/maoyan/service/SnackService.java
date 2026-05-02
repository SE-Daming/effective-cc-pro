package com.maoyan.service;

import com.maoyan.dto.snack.SnackOrderCreateDTO;
import com.maoyan.dto.snack.SnackQueryDTO;
import com.maoyan.dto.snack.SnackSaveDTO;
import com.maoyan.vo.PageVO;
import com.maoyan.vo.snack.*;

import java.util.List;

/**
 * 卖品服务接口
 *
 * @author maoyan
 */
public interface SnackService {

    /**
     * 获取卖品分类列表
     *
     * @return 分类列表
     */
    List<SnackCategoryVO> getCategories();

    /**
     * 获取卖品列表（C端）
     *
     * @param queryDTO 查询参数
     * @return 卖品列表
     */
    PageVO<SnackListVO> getSnackList(SnackQueryDTO queryDTO);

    /**
     * 获取卖品详情（C端）
     *
     * @param id 卖品ID
     * @return 卖品详情
     */
    SnackDetailVO getSnackDetail(Long id);

    /**
     * 创建卖品订单（C端）
     *
     * @param dto    订单请求
     * @param userId 用户ID
     * @return 订单创建结果
     */
    SnackOrderCreateVO createSnackOrder(SnackOrderCreateDTO dto, Long userId);

    /**
     * 获取卖品订单详情（C端）
     *
     * @param id     订单ID
     * @param userId 用户ID
     * @return 订单详情
     */
    SnackOrderDetailVO getSnackOrderDetail(Long id, Long userId);

    /**
     * 获取卖品订单列表（C端）
     *
     * @param status  订单状态
     * @param userId  用户ID
     * @param page    页码
     * @param pageSize 每页数量
     * @return 订单列表
     */
    PageVO<SnackOrderListVO> getSnackOrderList(Integer status, Long userId, Integer page, Integer pageSize);

    /**
     * 获取卖品列表（B端）
     *
     * @param queryDTO 查询参数
     * @return 卖品列表
     */
    PageVO<SnackListVO> getSnackListAdmin(SnackQueryDTO queryDTO);

    /**
     * 新增卖品（B端）
     *
     * @param dto 卖品信息
     * @return 卖品ID
     */
    Long createSnack(SnackSaveDTO dto);

    /**
     * 更新卖品（B端）
     *
     * @param id  卖品ID
     * @param dto 卖品信息
     */
    void updateSnack(Long id, SnackSaveDTO dto);

    /**
     * 删除卖品（B端）
     *
     * @param id 卖品ID
     */
    void deleteSnack(Long id);
}
