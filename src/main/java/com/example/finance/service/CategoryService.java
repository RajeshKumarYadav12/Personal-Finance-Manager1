package com.example.finance.service;
import com.example.finance.dto.category.CategoryRequest;
import com.example.finance.dto.category.CategoryResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.User;
import com.example.finance.exception.ConflictException;
import com.example.finance.exception.ForbiddenException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.UserRepository;
import com.example.finance.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;
  public CategoryService(CategoryRepository categoryRepository,
      UserRepository userRepository) {
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }
  @Transactional(readOnly = true)
  public List<CategoryResponse> getAllCategories() {
    User currentUser = getCurrentUser();
    List<Category> categories = categoryRepository.findAllAccessibleByUser(currentUser);
    return categories.stream()
        .map(this::mapToResponse)
        .collect(Collectors.toList());
  }
  public CategoryResponse createCategory(CategoryRequest request) {
    User currentUser = getCurrentUser();

    if (categoryRepository.existsByNameAndUser(request.getName(), currentUser)) {
      throw new ConflictException("Category with name '" + request.getName() + "' already exists");
    }

    if (categoryRepository.findByNameAndIsDefaultTrue(request.getName()).isPresent()) {
      throw new ConflictException("Category with name '" + request.getName() + "' already exists as default category");
    }
    Category category = new Category();
    category.setName(request.getName());
    category.setType(request.getType());
    category.setIsDefault(false);
    category.setUser(currentUser);
    Category savedCategory = categoryRepository.save(category);
    return mapToResponse(savedCategory);
  }
  public void deleteCategoryByName(String name) {
    User currentUser = getCurrentUser();

    Category category = categoryRepository.findByNameAndUser(name, currentUser)
        .orElseGet(() -> categoryRepository.findByNameAndIsDefaultTrue(name)
            .orElseThrow(() -> new ResourceNotFoundException("Category not found")));

    if (category.getIsDefault()) {
      throw new ForbiddenException("Cannot delete default category");
    }

    long transactionCount = categoryRepository.countTransactionsByCategory(category.getId());
    if (transactionCount > 0) {
      throw new ConflictException("Cannot delete category with existing transactions");
    }
    categoryRepository.delete(category);
  }
  public void deleteCategory(Long id) {
    Category category = categoryRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

    if (category.getIsDefault()) {
      throw new ForbiddenException("Cannot delete default category");
    }

    User currentUser = getCurrentUser();
    if (!category.getUser().getId().equals(currentUser.getId())) {
      throw new ForbiddenException("Cannot delete another user's category");
    }

    long transactionCount = categoryRepository.countTransactionsByCategory(id);
    if (transactionCount > 0) {
      throw new ConflictException("Cannot delete category with existing transactions");
    }
    categoryRepository.delete(category);
  }
  private User getCurrentUser() {
    String username = SecurityUtils.getCurrentUsername();
    return userRepository.findByUsername(username)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
  private CategoryResponse mapToResponse(Category category) {
    return new CategoryResponse(
        category.getName(),
        category.getType(),
        !category.getIsDefault());
  }
}
