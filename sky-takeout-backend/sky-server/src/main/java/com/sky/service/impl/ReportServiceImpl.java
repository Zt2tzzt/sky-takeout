package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;
    private final UserMapper userMapper;

    @Autowired
    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
    }

    /**
     * 此方法用于：获取日期列表字符串
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return String
     */
    private List<LocalDate> getDateList(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("Start date must be before or equal to end date");

        // 获取日期范围内所有日期字符串
        // 包括结束日期
        return Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(startDate.until(endDate).getDays() + 1) // 包括结束日期
                .collect(Collectors.toList());
    }

    /**
     * 此方法用于：营业额统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return TurnoverReportVO
     */
    @Override
    public TurnoverReportVO turnoverStatistics(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = getDateList(startDate, endDate);
        String dateListStr = StringUtils.join(dateList, ",");

        // 获取日期范围内所有营业额字符串
        List<Double> sumList = dateList.stream().map(date -> {
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            HashMap<String, Object> claim = new HashMap<>(Map.of(
                    "begin", begin,
                    "end", end,
                    "status", Orders.COMPLETED
            ));
            Double sum = orderMapper.sumByStatusAndOrderTime(claim);
            if (sum == null) sum = 0.0;
            return sum;
        }).toList();
        String sumListStr = StringUtils.join(sumList, ",");

        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(sumListStr)
                .build();
    }

    /**
     * 此方法用于：用户统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return UserReportVO
     */
    @Override
    public UserReportVO userStatistics(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = getDateList(startDate, endDate);
        String dateListStr = StringUtils.join(dateList, ",");

        // 获取每天的总用户数量、新增用户数量
        ArrayList<Integer> totalUserList = new ArrayList<>();
        ArrayList<Integer> newUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            // 计算当日的总用户数量
            Map<String, LocalDateTime> claim = new HashMap<>();
            claim.put("end", end);
            Integer totalCount = userMapper.countByMap(claim);
            totalUserList.add(totalCount);

            // 计算当日的新增用户数量
            claim.put("begin", begin);
            Integer newCount = userMapper.countByMap(claim);
            newUserList.add(newCount);
        }
        String totalUserListStr = StringUtils.join(totalUserList, ",");
        String newUserListStr = StringUtils.join(newUserList, ",");

        return UserReportVO.builder()
                .dateList(dateListStr)
                .totalUserList(totalUserListStr)
                .newUserList(newUserListStr)
                .build();
    }

    /**
     * 此方法用于：订单统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return OrderReportVO
     */
    @Override
    public OrderReportVO orderStatistics(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dateList = getDateList(startDate, endDate);
        String dateListStr = StringUtils.join(dateList, ",");

        // 获取每天的订单总数、有效订单数
        ArrayList<Integer> orderCountList = new ArrayList<>();
        ArrayList<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime begin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(date, LocalTime.MAX);

            // 查询当日总订单数
            HashMap<String, Object> claim = new HashMap<>();
            claim.put("begin", begin);
            claim.put("end", end);
            Integer orderCount = orderMapper.countByMap(claim);
            orderCountList.add(orderCount);

            // 查询当如订单总数
            claim.put("status", Orders.COMPLETED);
            Integer validOrderCount = orderMapper.countByMap(claim);
            validOrderCountList.add(validOrderCount);
        }
        String orderCountListStr = StringUtils.join(orderCountList, ",");
        String validOrderCountListStr = StringUtils.join(validOrderCountList, ",");

        // 计算时间区间内的订单总数
        Integer totalOrderCount = orderCountList.stream().reduce(0, Integer::sum);

        // 计算时间区间内的有效订单数
        Integer validOrderCount = validOrderCountList.stream().reduce(0, Integer::sum);

        // 计算订单完成率
        Double orderCompletionRate = totalOrderCount != 0 ? validOrderCount.doubleValue() / totalOrderCount : 0.0;

        return OrderReportVO.builder()
                .dateList(dateListStr)
                .orderCountList(orderCountListStr)
                .validOrderCountList(validOrderCountListStr)
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
}
