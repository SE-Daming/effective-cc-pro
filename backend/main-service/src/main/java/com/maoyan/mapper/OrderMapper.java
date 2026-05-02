package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}
