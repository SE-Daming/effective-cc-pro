package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.Movie;
import org.apache.ibatis.annotations.Mapper;

/**
 * 电影 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface MovieMapper extends BaseMapper<Movie> {

}
