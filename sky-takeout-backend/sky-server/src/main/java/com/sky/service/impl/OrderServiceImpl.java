package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.webSocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;
    private final AddressBookMapper addressBookMapper;
    private final ShoppingCartMapper shoppingCartMapper;
    private final WeChatPayUtil weChatPayUtil;
    private final UserMapper userMapper;
    private final WebSocketServer webSocketServer;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper, AddressBookMapper addressBookMapper, ShoppingCartMapper shoppingCartMapper, WeChatPayUtil weChatPayUtil, UserMapper userMapper, WebSocketServer webSocketServer) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
        this.addressBookMapper = addressBookMapper;
        this.shoppingCartMapper = shoppingCartMapper;
        this.weChatPayUtil = weChatPayUtil;
        this.userMapper = userMapper;
        this.webSocketServer = webSocketServer;
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

        // 在订单明细表中插入 n 条数据 orders
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

    /**
     * 此方法用于：订单支付
     *
     * @param ordersPaymentDTO 订单支付数据
     * @return OrderPaymentVO
     */
    @Override
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.selectById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal("0.01"), //支付金额，单位 元
                "ZeT1an的测试订单", //商品描述
                user.getOpenid() //微信用户的 openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID"))
            throw new OrderBusinessException("该订单已支付");

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 此方法用于：支付成功，修改订单状态
     *
     * @param outTradeNo 交易单号
     */
    @Override
    public void paySuccess(String outTradeNo) {
        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单 id 更新订单的状态、支付方式、支付状态、结账时间
        Long orderId = ordersDB.getId();
        Orders orders = Orders.builder()
                .id(orderId)
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.updateByIds(orders);

        // 通知管理端（浏览器）来单了
        HashMap<String, Object> claim = new HashMap<>(Map.of(
                "type", 1,
                "orderId", orderId,
                "content", "订单号：" + outTradeNo
        ));
        String res = JSON.toJSONString(claim);
        webSocketServer.sendToAllClient(res);
    }

    /**
     * 此方法用于：用户催单
     *
     * @param id 订单 id
     */
    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.selectById(id);

        if (orders == null)
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);

        HashMap<String, Object> claim = new HashMap<>() {{
            put("type", 2);
            put("orderId", id);
            put("content", "订单号：" + orders.getNumber());
        }};
        String res = JSON.toJSONString(claim);
        webSocketServer.sendToAllClient(res);
    }
}
