package com.example.finance.dto.report;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;
public class MonthlyReportResponse {
  private Integer year;
  private Integer month;
  private BigDecimal totalIncome;
  private BigDecimal totalExpenses;
  private BigDecimal netSavings;
  private Map<String, BigDecimal> categoryBreakdown = new LinkedHashMap<>();

  public MonthlyReportResponse() {
  }
  public MonthlyReportResponse(Integer year, Integer month, BigDecimal totalIncome,
      BigDecimal totalExpenses, BigDecimal netSavings, Map<String, BigDecimal> categoryBreakdown) {
    this.year = year;
    this.month = month;
    this.totalIncome = totalIncome;
    this.totalExpenses = totalExpenses;
    this.netSavings = netSavings;
    this.categoryBreakdown = categoryBreakdown != null ? categoryBreakdown : new LinkedHashMap<>();
  }

  public Integer getYear() {
    return year;
  }
  public void setYear(Integer year) {
    this.year = year;
  }
  public Integer getMonth() {
    return month;
  }
  public void setMonth(Integer month) {
    this.month = month;
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
}
