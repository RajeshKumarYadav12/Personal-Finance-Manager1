package com.example.finance.controller;

import com.example.finance.dto.goal.GoalRequest;
import com.example.finance.dto.goal.GoalResponse;
import com.example.finance.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
  private final GoalService goalService;

  public GoalController(GoalService goalService) {
    this.goalService = goalService;
  }

  @GetMapping
  public ResponseEntity<List<GoalResponse>> getAllGoals() {
    List<GoalResponse> goals = goalService.getAllGoals();
    return ResponseEntity.ok(goals);
  }

  @GetMapping("/{id}")
  public ResponseEntity<GoalResponse> getGoalById(@PathVariable Long id) {
    GoalResponse goal = goalService.getGoalById(id);
    return ResponseEntity.ok(goal);
  }

  @PostMapping
  public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
    GoalResponse response = goalService.createGoal(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<GoalResponse> updateGoal(
      @PathVariable Long id,
      @Valid @RequestBody GoalRequest request) {
    GoalResponse response = goalService.updateGoal(id, request);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Map<String, Object>> deleteGoal(@PathVariable Long id) {
    GoalResponse goal = goalService.deleteGoal(id);
    Map<String, Object> response = new HashMap<>();
    response.put("message", "Savings goal deleted successfully");
    response.put("id", goal.getId());
    response.put("goalName", goal.getGoalName());
    response.put("targetAmount", goal.getTargetAmount());
    return ResponseEntity.ok(response);
  }
}
