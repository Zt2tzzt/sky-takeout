package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkspaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;

@Service
@Slf4j
public class WorkspaceServiceImpl implements WorkspaceService {
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;
    private final DishMapper dishMapper;
    private final SetmealMapper setmealMapper;

    @Autowired
    public WorkspaceServiceImpl(OrderMapper orderMapper, UserMapper userMapper, DishMapper dishMapper, SetmealMapper setmealMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.dishMapper = dishMapper;
        this.setmealMapper = setmealMapper;
    }

    /**
     * 根据时间段统计营业数据
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return BusinessDataVO
     */
    public BusinessDataVO getBusinessData(LocalDateTime begin, LocalDateTime end) {
        /*
         * 营业额：当日已完成订单的总金额
         * 有效订单：当日已完成订单的数量
         * 订单完成率：有效订单数 / 总订单数
         * 平均客单价：营业额 / 有效订单数
         * 新增用户：当日新增用户的数量
         */

        //查询总订单数
        HashMap<String, Object> map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        Integer totalOrderCount = orderMapper.countByMap(map);

        //营业额
        map.put("status", Orders.COMPLETED);
        Double turnover = orderMapper.sumByStatusAndOrderTime(map);
        if (turnover == null) turnover = 0.0;

        //有效订单数
        Integer validOrderCount = orderMapper.countByMap(map);

        //平均客单价
        double unitPrice = 0.0;
        //订单完成率
        double orderCompletionRate = 0.0;
        if (totalOrderCount != 0 && validOrderCount != 0) {
            orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount;
            unitPrice = turnover / validOrderCount;
        }

        //新增用户数
        Integer newUsers = userMapper.countByMap(map);

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .newUsers(newUsers)
                .build();
    }


    /**
     * 查询订单管理数据
     *
     * @return OrderOverViewVO
     */
    public OrderOverViewVO getOrderOverView() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.now().with(LocalTime.MIN));
        map.put("status", Orders.TO_BE_CONFIRMED);

        //待接单
        Integer waitingOrders = orderMapper.countByMap(map);

        //待派送
        map.put("status", Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);

        //已完成
        map.put("status", Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);

        //已取消
        map.put("status", Orders.CANCELLED);
        Integer cancelledOrders = orderMapper.countByMap(map);

        //全部订单
        map.put("status", null);
        Integer allOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .completedOrders(completedOrders)
                .cancelledOrders(cancelledOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 查询菜品总览
     *
     * @return DishOverViewVO
     */
    public DishOverViewVO getDishOverView() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = dishMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = dishMapper.countByMap(map);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 查询套餐总览
     *
     * @return SetmealOverViewVO
     */
    public SetmealOverViewVO getSetmealOverView() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("status", StatusConstant.ENABLE);
        Integer sold = setmealMapper.countByMap(map);

        map.put("status", StatusConstant.DISABLE);
        Integer discontinued = setmealMapper.countByMap(map);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
