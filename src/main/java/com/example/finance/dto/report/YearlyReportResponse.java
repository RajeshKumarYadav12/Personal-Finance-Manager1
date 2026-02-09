package com.example.finance.dto.report;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class YearlyReportResponse {
  private Integer year;
  private BigDecimal totalIncome;
  private BigDecimal totalExpenses;
  private BigDecimal netSavings;
  private Map<String, BigDecimal> categoryBreakdown = new LinkedHashMap<>();
  private List<MonthlyReportResponse> monthlyBreakdown = new ArrayList<>();

  public YearlyReportResponse() {
  }
  public YearlyReportResponse(Integer year, BigDecimal totalIncome,
      BigDecimal totalExpenses, BigDecimal netSavings,
      Map<String, BigDecimal> categoryBreakdown,
      List<MonthlyReportResponse> monthlyBreakdown) {
    this.year = year;
    this.totalIncome = totalIncome;
    this.totalExpenses = totalExpenses;
    this.netSavings = netSavings;
    this.categoryBreakdown = categoryBreakdown != null ? categoryBreakdown : new LinkedHashMap<>();
    this.monthlyBreakdown = monthlyBreakdown;
  }

  public Integer getYear() {
    return year;
  }
  public void setYear(Integer year) {
    this.year = year;
  }
  public BigDecimal getTotalIncome() {
    return totalIncome;
  }
  public void setTotalIncome(BigDecimal totalIncome) {
    this.totalIncome = totalIncome;
  }
  public BigDecimal getTotalExpenses() {
    return totalExpenses;
  }
  public void setTotalExpenses(BigDecimal totalExpenses) {
    this.totalExpenses = totalExpenses;
  }
  public BigDecimal getNetSavings() {
    return netSavings;
  }
  public void setNetSavings(BigDecimal netSavings) {
    this.netSavings = netSavings;
  }
  public Map<String, BigDecimal> getCategoryBreakdown() {
    return categoryBreakdown;
  }
  public void setCategoryBreakdown(Map<String, BigDecimal> categoryBreakdown) {
    this.categoryBreakdown = categoryBreakdown;
  }
  public List<MonthlyReportResponse> getMonthlyBreakdown() {
    return monthlyBreakdown;
  }
  public void setMonthlyBreakdown(List<MonthlyReportResponse> monthlyBreakdown) {
    this.monthlyBreakdown = monthlyBreakdown;
  }
}
