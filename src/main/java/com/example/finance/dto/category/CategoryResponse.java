package com.example.finance.dto.category;
import com.example.finance.entity.Transaction;
public class CategoryResponse {
  private String name;
  private Transaction.TransactionType type;
  private Boolean isCustom;

  public CategoryResponse() {
  }
  public CategoryResponse(String name, Transaction.TransactionType type, Boolean isCustom) {
    this.name = name;
    this.type = type;
    this.isCustom = isCustom;
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
  public Boolean getIsCustom() {
    return isCustom;
  }
  public void setIsCustom(Boolean isCustom) {
    this.isCustom = isCustom;
  }
}
