package com.example.finance.util;
import java.time.LocalDate;
import java.time.YearMonth;
public class DateUtils {
  public static LocalDate getFirstDayOfMonth(int year, int month) {
    return LocalDate.of(year, month, 1);
  }
  public static LocalDate getLastDayOfMonth(int year, int month) {
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.atEndOfMonth();
  }
  public static LocalDate getFirstDayOfYear(int year) {
    return LocalDate.of(year, 1, 1);
  }
  public static LocalDate getLastDayOfYear(int year) {
    return LocalDate.of(year, 12, 31);
  }
  public static boolean isNotFutureDate(LocalDate date) {
    return !date.isAfter(LocalDate.now());
  }
}
