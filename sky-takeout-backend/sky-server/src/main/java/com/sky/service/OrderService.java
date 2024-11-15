package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;

public interface OrderService {
    /**
     * 此方法用于：用户下单
     *
     * @param ordersSubmitDTO 订单数据
     * @return OrderSubmitVO
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 此方法用于：订单支付
     *
     * @param ordersPaymentDTO 订单支付数据
     * @return OrderPaymentVO
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 此方法用于：支付成功，修改订单状态
     *
     * @param outTradeNo 交易单号
     */
    void paySuccess(String outTradeNo);

    /**
     * 此方法用于：用户催单
     *
     * @param id 订单 id
     */
    void reminder(Long id);
}
