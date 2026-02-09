package com.example.finance.controller;
import com.example.finance.dto.report.MonthlyReportResponse;
import com.example.finance.dto.report.YearlyReportResponse;
import com.example.finance.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/reports")
public class ReportController {
  private final ReportService reportService;
  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }
  @GetMapping("/monthly")
  public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
      @RequestParam int year,
      @RequestParam int month) {
    MonthlyReportResponse report = reportService.getMonthlyReport(year, month);
    return ResponseEntity.ok(report);
  }
  @GetMapping("/yearly")
  public ResponseEntity<YearlyReportResponse> getYearlyReport(@RequestParam int year) {
    YearlyReportResponse report = reportService.getYearlyReport(year);
    return ResponseEntity.ok(report);
  }
}
