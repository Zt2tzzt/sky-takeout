package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 此方法用于：用户下单
     *
     * @param ordersSubmitDTO 订单数据
     * @return OrderSubmitVO
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
