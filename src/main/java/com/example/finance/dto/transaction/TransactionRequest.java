package com.example.finance.dto.transaction;
import com.example.finance.entity.Transaction.TransactionType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public class TransactionRequest {
  @NotBlank(message = "Description is required")
  @Size(max = 255, message = "Description must not exceed 255 characters")
  private String description;
  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
  @Digits(integer = 17, fraction = 2, message = "Amount must have at most 2 decimal places")
  private BigDecimal amount;
  @NotNull(message = "Type is required")
  private TransactionType type;
  @NotNull(message = "Date is required")
  private LocalDate date;
  @NotBlank(message = "Category is required")
  private String category;

  public TransactionRequest() {
  }
  public TransactionRequest(String description, BigDecimal amount, TransactionType type,
      LocalDate date, String category) {
    this.description = description;
    this.amount = amount;
    this.type = type;
    this.date = date;
    this.category = category;
  }

  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public TransactionType getType() {
    return type;
  }
  public void setType(TransactionType type) {
    this.type = type;
  }
  public LocalDate getDate() {
    return date;
  }
  public void setDate(LocalDate date) {
    this.date = date;
  }
  public String getCategory() {
    return category;
  }
  public void setCategory(String category) {
    this.category = category;
  }
}
