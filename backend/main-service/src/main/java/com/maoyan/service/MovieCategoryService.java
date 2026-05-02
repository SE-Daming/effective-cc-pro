package com.maoyan.service;

import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.vo.movie.MovieCategoryVO;

import java.util.List;

/**
 * 电影分类服务接口
 *
 * @author maoyan
 */
public interface MovieCategoryService {

    /**
     * 获取所有电影分类
     *
     * @return 分类列表
     */
    List<MovieCategoryVO> getAllCategories();

    /**
     * 根据ID获取分类
     *
     * @param id 分类ID
     * @return 分类
     */
    MovieCategoryVO getCategoryById(Integer id);

    /**
     * 新增分类
     *
     * @param saveDTO 分类参数
     * @return 分类ID
     */
    Integer createCategory(MovieSaveDTO saveDTO);

    /**
     * 更新分类
     *
     * @param id      分类ID
     * @param saveDTO 分类参数
     */
    void updateCategory(Integer id, MovieSaveDTO saveDTO);

    /**
     * 删除分类
     *
     * @param id 分类ID
     */
    void deleteCategory(Integer id);

    /**
     * 根据分类ID列表获取分类名称
     *
     * @param categoryIds 分类ID（逗号分隔）
     * @return 分类名称（逗号分隔）
     */
    String getCategoryNames(String categoryIds);

}
