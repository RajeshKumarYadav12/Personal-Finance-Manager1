package com.example.finance.repository;
import com.example.finance.entity.Category;
import com.example.finance.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("SELECT c FROM Category c WHERE c.isDefault = true OR c.user = :user")
  List<Category> findAllAccessibleByUser(@Param("user") User user);
  Optional<Category> findByNameAndUser(String name, User user);
  Optional<Category> findByNameAndIsDefaultTrue(String name);
  boolean existsByNameAndUser(String name, User user);
  @Query("SELECT COUNT(t) FROM Transaction t WHERE t.category.id = :categoryId")
  long countTransactionsByCategory(@Param("categoryId") Long categoryId);
}
