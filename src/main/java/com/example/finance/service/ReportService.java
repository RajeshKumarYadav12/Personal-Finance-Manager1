package com.example.finance.service;
import com.example.finance.dto.report.MonthlyReportResponse;
import com.example.finance.dto.report.YearlyReportResponse;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.util.DateUtils;
import com.example.finance.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
@Transactional(readOnly = true)
public class ReportService {
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  public ReportService(TransactionRepository transactionRepository,
      UserRepository userRepository) {
    this.transactionRepository = transactionRepository;
    this.userRepository = userRepository;
  }
  public MonthlyReportResponse getMonthlyReport(int year, int month) {

    if (month < 1 || month > 12) {
      throw new IllegalArgumentException("Month must be between 1 and 12");
    }
    User currentUser = getCurrentUser();
    LocalDate startDate = DateUtils.getFirstDayOfMonth(year, month);
    LocalDate endDate = DateUtils.getLastDayOfMonth(year, month);
    BigDecimal totalIncome = transactionRepository.calculateTotalIncome(
        currentUser, startDate, endDate);
    BigDecimal totalExpenses = transactionRepository.calculateTotalExpenses(
        currentUser, startDate, endDate);
    BigDecimal netSavings = totalIncome.subtract(totalExpenses);

    Map<String, BigDecimal> categoryBreakdown = calculateCategoryBreakdown(
        currentUser, startDate, endDate);
    return new MonthlyReportResponse(
        year,
        month,
        totalIncome,
        totalExpenses,
        netSavings,
        categoryBreakdown);
  }
  public YearlyReportResponse getYearlyReport(int year) {
    User currentUser = getCurrentUser();
    LocalDate yearStart = DateUtils.getFirstDayOfYear(year);
    LocalDate yearEnd = DateUtils.getLastDayOfYear(year);

    BigDecimal yearlyIncome = transactionRepository.calculateTotalIncome(
        currentUser, yearStart, yearEnd);
    BigDecimal yearlyExpenses = transactionRepository.calculateTotalExpenses(
        currentUser, yearStart, yearEnd);
    BigDecimal yearlyNetSavings = yearlyIncome.subtract(yearlyExpenses);

    Map<String, BigDecimal> categoryBreakdown = calculateCategoryBreakdown(
        currentUser, yearStart, yearEnd);

    List<MonthlyReportResponse> monthlyBreakdown = new ArrayList<>();
    for (int month = 1; month <= 12; month++) {
      MonthlyReportResponse monthlyReport = getMonthlyReport(year, month);
      monthlyBreakdown.add(monthlyReport);
    }
    return new YearlyReportResponse(
        year,
        yearlyIncome,
        yearlyExpenses,
        yearlyNetSavings,
        categoryBreakdown,
        monthlyBreakdown);
  }
  private Map<String, BigDecimal> calculateCategoryBreakdown(
      User user, LocalDate startDate, LocalDate endDate) {
    List<Transaction> transactions = transactionRepository.findByUserWithFilters(
        user, startDate, endDate);
    return transactions.stream()
        .collect(Collectors.groupingBy(
            transaction -> transaction.getCategory().getName(),
            LinkedHashMap::new,
            Collectors.reducing(
                BigDecimal.ZERO,
                Transaction::getAmount,
                BigDecimal::add)));
  }
  private User getCurrentUser() {
    String username = SecurityUtils.getCurrentUsername();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
