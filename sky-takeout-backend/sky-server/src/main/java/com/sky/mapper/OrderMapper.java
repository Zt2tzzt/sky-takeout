package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    /**
     * 此方法用于：插入订单记录
     *
     * @param orders 订单实体类
     * @return int
     */
    int insert(Orders orders);
}
