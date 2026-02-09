package com.example.finance.repository;
import com.example.finance.entity.Transaction;
import com.example.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
  List<Transaction> findByUserOrderByDateDesc(User user);
  Optional<Transaction> findByIdAndUser(Long id, User user);
  @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
      "AND (:startDate IS NULL OR t.date >= :startDate) " +
      "AND (:endDate IS NULL OR t.date <= :endDate) " +
      "ORDER BY t.date DESC")
  List<Transaction> findByUserWithFilters(
      @Param("user") User user,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
      "WHERE t.user = :user AND t.type = 'INCOME' " +
      "AND t.date >= :startDate AND t.date <= :endDate")
  BigDecimal calculateTotalIncome(
      @Param("user") User user,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
      "WHERE t.user = :user AND t.type = 'EXPENSE' " +
      "AND t.date >= :startDate AND t.date <= :endDate")
  BigDecimal calculateTotalExpenses(
      @Param("user") User user,
      @Param("startDate") LocalDate startDate,
      @Param("endDate") LocalDate endDate);
  void deleteByUser(User user);
}
