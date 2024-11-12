package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
    }

    @Transactional
    @Override
    public OrderSubmitVO submitOrder(@NotNull OrdersSubmitDTO ordersSubmitDTO) {
        // 处理各种业务异常（地址为空等，购物车为空）
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null)
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);

        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.selectByShoppingCart(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.isEmpty())
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);

        // 在订单表中插入数据
        Orders orders = Orders.builder()
                .orderTime(LocalDateTime.now())
                .payStatus(Orders.UN_PAID)
                .status(Orders.PENDING_PAYMENT)
                .number(String.valueOf(System.currentTimeMillis()))
                .phone(addressBook.getPhone())
                .consignee(addressBook.getConsignee())
                .userId(userId)
                .build();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        int i = orderMapper.insert(orders);
        log.info("插入订单数据：{} 条", i);

        // 在订单明细表中插入 n 条数据orders
        List<OrderDetail> orderDetailList = shoppingCarts.stream().map(sc -> {
            OrderDetail orderDetail = OrderDetail.builder().orderId(orders.getId()).build();
            BeanUtils.copyProperties(sc, orderDetail);
            return orderDetail;
        }).toList();
        int j = orderDetailMapper.insertBatch(orderDetailList);
        log.info("插入订单明细数据：{} 条", j);

        // 清空购物车
        int k = shoppingCartMapper.deleteByUserId(userId);
        log.info("清空购物车：{} 条", k);

        // 封装返回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }
}
