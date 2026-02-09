package com.example.finance.controller;
import com.example.finance.dto.transaction.TransactionRequest;
import com.example.finance.dto.transaction.TransactionResponse;
import com.example.finance.entity.Transaction.TransactionType;
import com.example.finance.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {
  private final TransactionService transactionService;
  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }
  @GetMapping
  public ResponseEntity<List<TransactionResponse>> getAllTransactions(
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
      @RequestParam(required = false) String category,
      @RequestParam(required = false) TransactionType type) {
    List<TransactionResponse> transactions = transactionService.getAllTransactions(
        startDate, endDate, category, type);
    return ResponseEntity.ok(transactions);
  }
  @GetMapping("/{id}")
  public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
    TransactionResponse transaction = transactionService.getTransactionById(id);
    return ResponseEntity.ok(transaction);
  }
  @PostMapping
  public ResponseEntity<TransactionResponse> createTransaction(
      @Valid @RequestBody TransactionRequest request) {
    TransactionResponse response = transactionService.createTransaction(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
  @PutMapping("/{id}")
  public ResponseEntity<TransactionResponse> updateTransaction(
      @PathVariable Long id,
      @Valid @RequestBody TransactionRequest request) {
    TransactionResponse response = transactionService.updateTransaction(id, request);
    return ResponseEntity.ok(response);
  }
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
    transactionService.deleteTransaction(id);
    return ResponseEntity.noContent().build();
  }
}
