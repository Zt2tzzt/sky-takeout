package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.service.ReportService;
import com.sky.vo.TurnoverReportVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {
    private final OrderMapper orderMapper;

    @Autowired
    public ReportServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
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
        if (startDate.isAfter(endDate))
            throw new IllegalArgumentException("Start date must be before or equal to end date");

        // 获取日期范围内所有日期字符串
        List<LocalDate> dateList = Stream.iterate(startDate, date -> date.plusDays(1))
                .limit(startDate.until(endDate).getDays() + 1) // 包括结束日期
                .collect(Collectors.toList());
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
}
