package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    /**
     * 此方法用于：批量插入订单明细数据
     * @param orderDetailList 订单明细数据
     * @return int
     */
    int insertBatch(List<OrderDetail> orderDetailList);
}
