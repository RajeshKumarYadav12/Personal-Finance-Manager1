package com.example.finance.service;

import com.example.finance.dto.transaction.TransactionRequest;
import com.example.finance.dto.transaction.TransactionResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.Transaction.TransactionType;
import com.example.finance.entity.User;
import com.example.finance.exception.ForbiddenException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.TransactionRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public TransactionService(TransactionRepository transactionRepository,
      CategoryRepository categoryRepository,
      UserRepository userRepository) {
    this.transactionRepository = transactionRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public List<TransactionResponse> getAllTransactions(LocalDate startDate, LocalDate endDate,
      String category, TransactionType type) {
    User currentUser = getCurrentUser();
    List<Transaction> transactions;

    transactions = transactionRepository.findByUserWithFilters(currentUser, startDate, endDate);

    if (category != null) {
      transactions = transactions.stream()
          .filter(t -> t.getCategory().getName().equals(category))
          .collect(Collectors.toList());
    }
    if (type != null) {
      transactions = transactions.stream()
          .filter(t -> t.getType().equals(type))
          .collect(Collectors.toList());
    }
    return transactions.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public TransactionResponse getTransactionById(Long id) {
    User currentUser = getCurrentUser();
    Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    return mapToResponse(transaction);
  }

  public TransactionResponse createTransaction(TransactionRequest request) {
    User currentUser = getCurrentUser();

    Category category = validateCategoryAccess(request.getCategory(), currentUser);
    Transaction transaction = new Transaction();
    transaction.setDescription(request.getDescription());
    transaction.setAmount(request.getAmount());
    transaction.setType(request.getType());
    transaction.setDate(request.getDate());
    transaction.setCategory(category);
    transaction.setUser(currentUser);
    Transaction savedTransaction = transactionRepository.save(transaction);
    return mapToResponse(savedTransaction);
  }

  public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
    User currentUser = getCurrentUser();
    Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

    if (!transaction.getDate().equals(request.getDate())) {
      throw new ForbiddenException("Transaction date cannot be modified");
    }

    Category category = validateCategoryAccess(request.getCategory(), currentUser);
    transaction.setDescription(request.getDescription());
    transaction.setAmount(request.getAmount());
    transaction.setType(request.getType());
    transaction.setCategory(category);
    Transaction updatedTransaction = transactionRepository.save(transaction);
    return mapToResponse(updatedTransaction);
  }

  public TransactionResponse deleteTransaction(Long id) {
    User currentUser = getCurrentUser();
    Transaction transaction = transactionRepository.findByIdAndUser(id, currentUser)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    TransactionResponse response = mapToResponse(transaction);
    transactionRepository.delete(transaction);
    return response;
  }

  private Category validateCategoryAccess(String categoryName, User user) {

    Category category = categoryRepository.findByNameAndUser(categoryName, user)
        .orElseGet(() -> categoryRepository.findByNameAndIsDefaultTrue(categoryName)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found")));
    return category;
  }

  private User getCurrentUser() {
    String username = SecurityUtils.getCurrentUsername();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }

  private TransactionResponse mapToResponse(Transaction transaction) {
    return new TransactionResponse(
        transaction.getId(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getType(),
        transaction.getDate(),
        transaction.getCategory().getName());
  }
}
