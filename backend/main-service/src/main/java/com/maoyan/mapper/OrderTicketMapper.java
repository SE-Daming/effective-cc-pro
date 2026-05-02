package com.maoyan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.maoyan.entity.OrderTicket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单票务明细 Mapper
 *
 * @author maoyan
 */
@Mapper
public interface OrderTicketMapper extends BaseMapper<OrderTicket> {

}
