package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
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
    private final WorkspaceService workspaceService;

    @Autowired
    public ReportServiceImpl(OrderMapper orderMapper, UserMapper userMapper, WorkspaceService workspaceService) {
        this.orderMapper = orderMapper;
        this.userMapper = userMapper;
        this.workspaceService = workspaceService;
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
            HashMap<String, Object> claim = new HashMap<>();
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

            // 查询当日有效订单总数
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

    /**
     * 此方法用于：销量排名
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return SalesTop10ReportVO
     */
    @Override
    public SalesTop10ReportVO top10(LocalDate startDate, LocalDate endDate) {
        LocalDateTime begin = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);

        List<GoodsSalesDTO> list = orderMapper.selectSalesTop(begin, end);

        // 获取商品名称字符串
        List<String> names = list.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameListStr = StringUtils.join(names, ",");

        // 获取商品数量字符串
        List<Integer> sales = list.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberListStr = StringUtils.join(sales, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameListStr)
                .numberList(numberListStr)
                .build();
    }

    /**
     * 此方法用于：导出运营数据报表
     *
     * @param response HttpServletResponse
     */
    @Override
    public void exportBusinessData(HttpServletResponse response) throws IOException {
        // 获取最近 30 天的开始时间、结束时间
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(1);

        // 查询概览数据
        LocalDateTime begin = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime end = LocalDateTime.of(endDate, LocalTime.MAX);
        BusinessDataVO businessDataVO = workspaceService.getBusinessData(begin, end);

        // 写入到 Excel 文件中
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        // 基于模板文件，在内存中创建 Excel 文件
        if (is == null) return;
        XSSFWorkbook xlsx = new XSSFWorkbook(is);

        // 表格设置时间范围
        XSSFSheet sheet1 = xlsx.getSheet("Sheet1");
        XSSFRow row = sheet1.getRow(1);
        XSSFCell cell1 = row.getCell(1);
        cell1.setCellValue("时间：" + startDate + " 至 " + endDate);

        row = sheet1.getRow(3); // 获取第 4 行
        // 表格设置营业额
        row.getCell(2).setCellValue(businessDataVO.getTurnover());
        // 表格设置订单完成率
        row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
        // 表格设置新增用户数
        row.getCell(6).setCellValue(businessDataVO.getNewUsers());

        row = sheet1.getRow(4); // 获取第 5 行
        // 表格设置有效订单数
        row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
        // 表格设置平均客单价
        row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

        // 表格设置明细数
        List<LocalDate> dateList = getDateList(startDate, endDate);
        LocalDate date;
        for (int i = 0; i < dateList.size(); i++) {
            // 查询某一天的营业数据
            date = dateList.get(i);
            begin = LocalDateTime.of(date, LocalTime.MIN);
            end = LocalDateTime.of(date, LocalTime.MAX);
            businessDataVO = workspaceService.getBusinessData(begin, end);

            row = sheet1.getRow(i + 7);
            row.getCell(1).setCellValue(date.toString());
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(3).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(5).setCellValue(businessDataVO.getUnitPrice());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());
        }

        // 输出流写出文件给客户端。
        ServletOutputStream os = response.getOutputStream();
        xlsx.write(os);

        // 释放资源
        os.close();
        is.close();
        xlsx.close();
    }
}
