package com.example.finance.dto.goal;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public class GoalRequest {
  @NotBlank(message = "Goal name is required")
  @Size(max = 100, message = "Goal name must not exceed 100 characters")
  private String goalName;
  @NotNull(message = "Target amount is required")
  @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
  @Digits(integer = 17, fraction = 2, message = "Target amount must have at most 2 decimal places")
  private BigDecimal targetAmount;
  @NotNull(message = "Start date is required")
  private LocalDate startDate;
  @NotNull(message = "Target date is required")
  private LocalDate targetDate;

  public GoalRequest() {
  }
  public GoalRequest(String goalName, BigDecimal targetAmount, LocalDate startDate, LocalDate targetDate) {
    this.goalName = goalName;
    this.targetAmount = targetAmount;
    this.startDate = startDate;
    this.targetDate = targetDate;
  }

  public String getGoalName() {
    return goalName;
  }
  public void setGoalName(String goalName) {
    this.goalName = goalName;
  }
  public BigDecimal getTargetAmount() {
    return targetAmount;
  }
  public void setTargetAmount(BigDecimal targetAmount) {
    this.targetAmount = targetAmount;
  }
  public LocalDate getStartDate() {
    return startDate;
  }
  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }
  public LocalDate getTargetDate() {
    return targetDate;
  }
  public void setTargetDate(LocalDate targetDate) {
    this.targetDate = targetDate;
  }
}
