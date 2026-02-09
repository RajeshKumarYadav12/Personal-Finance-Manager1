package com.example.finance.service;
import com.example.finance.dto.goal.GoalRequest;
import com.example.finance.dto.goal.GoalResponse;
import com.example.finance.entity.SavingsGoal;
import com.example.finance.entity.User;
import com.example.finance.exception.ForbiddenException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.GoalRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class GoalService {
  private final GoalRepository goalRepository;
  private final TransactionRepository transactionRepository;
  private final UserRepository userRepository;
  public GoalService(GoalRepository goalRepository,
      TransactionRepository transactionRepository,
      UserRepository userRepository) {
    this.goalRepository = goalRepository;
    this.transactionRepository = transactionRepository;
    this.userRepository = userRepository;
  }
  @Transactional(readOnly = true)
  public List<GoalResponse> getAllGoals() {
    User currentUser = getCurrentUser();
    List<SavingsGoal> goals = goalRepository.findByUser(currentUser);
    return goals.stream()
        .map(goal -> mapToResponse(goal, currentUser))
        .collect(Collectors.toList());
  }
  @Transactional(readOnly = true)
  public GoalResponse getGoalById(Long id) {
    User currentUser = getCurrentUser();
    SavingsGoal goal = goalRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));
    return mapToResponse(goal, currentUser);
  }
  public GoalResponse createGoal(GoalRequest request) {
    User currentUser = getCurrentUser();

    if (request.getTargetDate().isBefore(request.getStartDate())) {
      throw new IllegalArgumentException("Target date must be after start date");
    }
    SavingsGoal goal = new SavingsGoal();
    goal.setGoalName(request.getGoalName());
    goal.setTargetAmount(request.getTargetAmount());
    goal.setStartDate(request.getStartDate());
    goal.setTargetDate(request.getTargetDate());
    goal.setUser(currentUser);
    SavingsGoal savedGoal = goalRepository.save(goal);
    return mapToResponse(savedGoal, currentUser);
  }
  public GoalResponse updateGoal(Long id, GoalRequest request) {
    User currentUser = getCurrentUser();
    SavingsGoal goal = goalRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));

    if (request.getTargetDate().isBefore(request.getStartDate())) {
      throw new IllegalArgumentException("Target date must be after start date");
    }
    goal.setGoalName(request.getGoalName());
    goal.setTargetAmount(request.getTargetAmount());
    goal.setStartDate(request.getStartDate());
    goal.setTargetDate(request.getTargetDate());
    SavingsGoal updatedGoal = goalRepository.save(goal);
    return mapToResponse(updatedGoal, currentUser);
  }
  public GoalResponse deleteGoal(Long id) {
    User currentUser = getCurrentUser();
    SavingsGoal goal = goalRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Savings goal not found"));
    GoalResponse response = mapToResponse(goal, currentUser);
    goalRepository.delete(goal);
    return response;
  }
  private BigDecimal calculateProgress(SavingsGoal goal, User user) {
    LocalDate endDate = LocalDate.now().isBefore(goal.getTargetDate())
        ? LocalDate.now()
        : goal.getTargetDate();
    BigDecimal totalIncome = transactionRepository.calculateTotalIncome(
        user, goal.getStartDate(), endDate);
    BigDecimal totalExpenses = transactionRepository.calculateTotalExpenses(
        user, goal.getStartDate(), endDate);
    return totalIncome.subtract(totalExpenses);
  }
  private User getCurrentUser() {
    String username = SecurityUtils.getCurrentUsername();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
  private GoalResponse mapToResponse(SavingsGoal goal, User user) {
    BigDecimal currentProgress = calculateProgress(goal, user);
    BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentProgress);

    double percentageComplete = 0.0;
    if (goal.getTargetAmount().compareTo(BigDecimal.ZERO) > 0) {
      percentageComplete = currentProgress
          .divide(goal.getTargetAmount(), 4, RoundingMode.HALF_UP)
          .multiply(new BigDecimal("100"))
          .doubleValue();
    }

    percentageComplete = Math.max(0.0, Math.min(100.0, percentageComplete));
    return new GoalResponse(
        goal.getId(),
        goal.getGoalName(),
        goal.getTargetAmount(),
        goal.getStartDate(),
        goal.getTargetDate(),
        currentProgress,
        remainingAmount,
        percentageComplete);
  }
}
