package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.UserFavorite;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户收藏 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface UserFavoriteMapper extends BaseMapper<UserFavorite> {

}
