package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.MovieReview;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电影影评 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface MovieReviewMapper extends BaseMapper<MovieReview> {

}
