package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

public interface ReportService {
    /**
     * 此方法用于：营业额统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return TurnoverReportVO
     */
    TurnoverReportVO turnoverStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 此方法用于：用户统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return UserReportVO
     */
    UserReportVO userStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 此方法用于：订单统计
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return OrderReportVO
     */
    OrderReportVO orderStatistics(LocalDate startDate, LocalDate endDate);

    /**
     * 此方法用于：销量排名
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return SalesTop10ReportVO
     */
    SalesTop10ReportVO top10(LocalDate startDate, LocalDate endDate);

    /**
     * 此方法用于：导出运营数据报表
     *
     * @param response HttpServletResponse
     */
    void exportBusinessData(HttpServletResponse response) throws IOException;
}
