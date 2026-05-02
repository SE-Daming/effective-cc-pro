package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
