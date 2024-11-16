package com.sky.service;

import com.sky.vo.TurnoverReportVO;

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
}
