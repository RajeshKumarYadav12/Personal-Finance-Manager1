package com.example.finance.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Table(name = "transaction")
public class Transaction {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String description;
  @Column(nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private TransactionType type;
  @Column(nullable = false, updatable = false)
  private LocalDate date;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  public Transaction() {
  }
  public Transaction(String description, BigDecimal amount, TransactionType type,
      LocalDate date, Category category, User user) {
    this.description = description;
    this.amount = amount;
    this.type = type;
    this.date = date;
    this.category = category;
    this.user = user;
  }

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
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
  public Category getCategory() {
    return category;
  }
  public void setCategory(Category category) {
    this.category = category;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public enum TransactionType {
    INCOME, EXPENSE
  }
}
