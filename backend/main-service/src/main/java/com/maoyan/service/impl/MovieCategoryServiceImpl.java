package com.maoyan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.maoyan.common.ResultCode;
import com.maoyan.dto.movie.MovieSaveDTO;
import com.maoyan.entity.MovieCategory;
import com.maoyan.exception.BusinessException;
import com.maoyan.mapper.MovieCategoryMapper;
import com.maoyan.service.MovieCategoryService;
import com.maoyan.vo.movie.MovieCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电影分类服务实现
 *
 * @author maoyan
 */
@Service
@RequiredArgsConstructor
public class MovieCategoryServiceImpl implements MovieCategoryService {

    private final MovieCategoryMapper movieCategoryMapper;

    @Override
    public List<MovieCategoryVO> getAllCategories() {
        List<MovieCategory> categories = movieCategoryMapper.selectList(
                new LambdaQueryWrapper<MovieCategory>()
                        .orderByAsc(MovieCategory::getSort)
        );
        return categories.stream().map(this::toCategoryVO).collect(Collectors.toList());
    }

    @Override
    public MovieCategoryVO getCategoryById(Integer id) {
        MovieCategory category = movieCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "分类不存在");
        }
        return toCategoryVO(category);
    }

    @Override
    public Integer createCategory(MovieSaveDTO saveDTO) {
        MovieCategory category = new MovieCategory();
        category.setName(saveDTO.getTitle());
        category.setSort(saveDTO.getSort() != null ? saveDTO.getSort() : 0);
        movieCategoryMapper.insert(category);
        return category.getId();
    }

    @Override
    public void updateCategory(Integer id, MovieSaveDTO saveDTO) {
        MovieCategory category = movieCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ResultCode.NOT_FOUND.getCode(), "分类不存在");
        }
        category.setName(saveDTO.getTitle());
        if (saveDTO.getSort() != null) {
            category.setSort(saveDTO.getSort());
        }
        movieCategoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Integer id) {
        movieCategoryMapper.deleteById(id);
    }

    @Override
    public String getCategoryNames(String categoryIds) {
        if (StrUtil.isBlank(categoryIds)) {
            return "";
        }
        List<Integer> ids = Arrays.stream(categoryIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<MovieCategory> categories = movieCategoryMapper.selectBatchIds(ids);
        return categories.stream()
                .map(MovieCategory::getName)
                .collect(Collectors.joining(","));
    }

    private MovieCategoryVO toCategoryVO(MovieCategory category) {
        MovieCategoryVO vo = new MovieCategoryVO();
        vo.setId(category.getId());
        vo.setName(category.getName());
        vo.setSort(category.getSort());
        return vo;
    }

}
