package com.example.finance.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "savings_goal")
public class SavingsGoal {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String goalName;
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal targetAmount;
  @Column(nullable = false)
  private LocalDate startDate;
  @Column(nullable = false)
  private LocalDate targetDate;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public SavingsGoal() {
  }
  public SavingsGoal(String goalName, BigDecimal targetAmount, LocalDate startDate,
      LocalDate targetDate, User user) {
    this.goalName = goalName;
    this.targetAmount = targetAmount;
    this.startDate = startDate;
    this.targetDate = targetDate;
    this.user = user;
  }

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
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
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
}
