package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    private final OrderMapper orderMapper;

    @Autowired
    public OrderTask(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 此方法用于：处理超时订单；每分钟执行一次。
     */
    //@Scheduled(cron = "0/5 * * * * ? ")
    @Scheduled(cron = "0 * * * * ? ")
    public void processTimeoutOrder() {
        LocalDateTime now = LocalDateTime.now();
        log.info("定时任务开始处理超时订单，{}", now);

        List<Orders> orders = orderMapper.selectByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, now.minusMinutes(15));

        if (orders == null || orders.isEmpty()) return;

        int i = 0;
        for (Orders order : orders) {
            order.setStatus(Orders.CANCELLED);
            order.setCancelReason("订单超时, 自动取消");
            order.setCancelTime(now);
            i += orderMapper.updateByIds(order);
        }

        log.info("定时任务处理超时订单，共 {} 条", i);
    }

    /**
     * 此方法用于：处理自动派送订单；每天凌晨 1 点执行一次。
     */
    //@Scheduled(cron = "1/5 * * * * ? ")
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder() {
        LocalDateTime now = LocalDateTime.now();
        log.info("定时任务开始处理状态为派送的订单，{}", now);

        List<Orders> orders = orderMapper.selectByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, now.minusHours(1));

        if (orders == null || orders.isEmpty()) return;

        int i = 0;
        for (Orders order : orders) {
            order.setStatus(Orders.COMPLETED);
            i += orderMapper.updateByIds(order);
        }
        log.info("定时任务处理状态为派送的订单，共 {} 条", i);
    }
}
