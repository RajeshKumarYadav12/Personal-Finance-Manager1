package com.example.finance.entity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "category", uniqueConstraints = {
    @UniqueConstraint(columnNames = { "name", "user_id" })
})
public class Category {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private String name;
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private Transaction.TransactionType type;
  @Column(nullable = false)
  private Boolean isDefault = false;
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;
  @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
  private List<Transaction> transactions = new ArrayList<>();

  public Category() {
  }
  public Category(String name, Transaction.TransactionType type, Boolean isDefault, User user) {
    this.name = name;
    this.type = type;
    this.isDefault = isDefault;
    this.user = user;
  }

  public Long getId() {
    return id;
  }
  public void setId(Long id) {
    this.id = id;
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
  public Boolean getIsDefault() {
    return isDefault;
  }
  public void setIsDefault(Boolean isDefault) {
    this.isDefault = isDefault;
  }
  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }
  public List<Transaction> getTransactions() {
    return transactions;
  }
  public void setTransactions(List<Transaction> transactions) {
    this.transactions = transactions;
  }
}
