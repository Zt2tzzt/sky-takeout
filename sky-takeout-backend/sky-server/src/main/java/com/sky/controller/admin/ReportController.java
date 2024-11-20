package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/report")
@Slf4j
@Tag(name = "数据统计相关接口")
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/turnoverStatistics")
    @Operation(summary = "营业额统计")
    public Result<TurnoverReportVO> turnoverStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("begin") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("end") LocalDate endDate
    ) {
        log.info("营业额统计，开始日期：{}，结束日期：{}", startDate, endDate);

        TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(startDate, endDate);
        return Result.success(turnoverReportVO);
    }

    @GetMapping("/userStatistics")
    @Operation(summary = "用户统计")
    public Result<UserReportVO> userStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("begin") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("end") LocalDate endDate
    ) {
        log.info("用户统计，开始日期：{}，结束日期：{}", startDate, endDate);

        UserReportVO userReportVO = reportService.userStatistics(startDate, endDate);
        return Result.success(userReportVO);
    }

    @GetMapping("/ordersStatistics")
    @Operation(summary = "订单统计")
    public Result<OrderReportVO> orderStatistics(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("begin") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("end") LocalDate endDate
    ) {
        log.info("订单统计，开始日期：{}，结束日期：{}", startDate, endDate);

        OrderReportVO orderReportVO = reportService.orderStatistics(startDate, endDate);
        return Result.success(orderReportVO);
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("begin") LocalDate startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam("end") LocalDate endDate
    ) {
        log.info("查询销量排名top10，开始日期：{}，结束日期：{}", startDate, endDate);
        return Result.success(reportService.top10(startDate, endDate));
    }
}
