package com.example.finance.dto.category;
import com.example.finance.entity.Transaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
public class CategoryRequest {
  @NotBlank(message = "Category name is required")
  @Size(min = 1, max = 50, message = "Category name must be between 1 and 50 characters")
  private String name;
  @NotNull(message = "Category type is required")
  private Transaction.TransactionType type;

  public CategoryRequest() {
  }
  public CategoryRequest(String name, Transaction.TransactionType type) {
    this.name = name;
    this.type = type;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Transaction.TransactionType getType() {
    return type;
  }
  public void setType(Transaction.TransactionType type) {
    this.type = type;
  }
}
