package com.example.finance.service;
import com.example.finance.dto.category.CategoryRequest;
import com.example.finance.dto.category.CategoryResponse;
import com.example.finance.entity.Category;
import com.example.finance.entity.Transaction.TransactionType;
import com.example.finance.entity.User;
import com.example.finance.exception.ConflictException;
import com.example.finance.exception.ForbiddenException;
import com.example.finance.exception.ResourceNotFoundException;
import com.example.finance.repository.CategoryRepository;
import com.example.finance.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.finance.util.SecurityUtils;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {
  @Mock
  private CategoryRepository categoryRepository;
  @Mock
  private UserRepository userRepository;
  @InjectMocks
  private CategoryService categoryService;
  private User testUser;
  private Category defaultCategory;
  private Category customCategory;
  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testuser");
    defaultCategory = new Category();
    defaultCategory.setId(1L);
    defaultCategory.setName("Salary");
    defaultCategory.setIsDefault(true);
    customCategory = new Category();
    customCategory.setId(2L);
    customCategory.setName("Custom");
    customCategory.setIsDefault(false);
    customCategory.setUser(testUser);
  }
  @Test
  void getAllCategories_Success() {

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(categoryRepository.findAllAccessibleByUser(testUser))
          .thenReturn(Arrays.asList(defaultCategory, customCategory));

      List<CategoryResponse> categories = categoryService.getAllCategories();

      assertNotNull(categories);
      assertEquals(2, categories.size());
      verify(categoryRepository, times(1)).findAllAccessibleByUser(testUser);
    }
  }
  @Test
  void createCategory_Success() {

    CategoryRequest request = new CategoryRequest("NewCategory", TransactionType.EXPENSE);
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(categoryRepository.existsByNameAndUser("NewCategory", testUser)).thenReturn(false);
      when(categoryRepository.findByNameAndIsDefaultTrue("NewCategory")).thenReturn(Optional.empty());
      when(categoryRepository.save(any(Category.class))).thenReturn(customCategory);

      CategoryResponse response = categoryService.createCategory(request);

      assertNotNull(response);
      verify(categoryRepository, times(1)).save(any(Category.class));
    }
  }
  @Test
  void createCategory_DuplicateName_ThrowsConflictException() {

    CategoryRequest request = new CategoryRequest("Custom", TransactionType.EXPENSE);
    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(categoryRepository.existsByNameAndUser("Custom", testUser)).thenReturn(true);

      assertThrows(ConflictException.class, () -> categoryService.createCategory(request));
      verify(categoryRepository, never()).save(any(Category.class));
    }
  }
  @Test
  void deleteCategory_Success() {

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(categoryRepository.findById(2L)).thenReturn(Optional.of(customCategory));
      when(categoryRepository.countTransactionsByCategory(2L)).thenReturn(0L);

      categoryService.deleteCategory(2L);

      verify(categoryRepository, times(1)).delete(customCategory);
    }
  }
  @Test
  void deleteCategory_DefaultCategory_ThrowsForbiddenException() {

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(categoryRepository.findById(1L)).thenReturn(Optional.of(defaultCategory));

      assertThrows(ForbiddenException.class, () -> categoryService.deleteCategory(1L));
      verify(categoryRepository, never()).delete(any(Category.class));
    }
  }
  @Test
  void deleteCategory_HasTransactions_ThrowsConflictException() {

    try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
      mockedSecurityUtils.when(SecurityUtils::getCurrentUsername).thenReturn("testuser");
      when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
      when(categoryRepository.findById(2L)).thenReturn(Optional.of(customCategory));
      when(categoryRepository.countTransactionsByCategory(2L)).thenReturn(5L);

      assertThrows(ConflictException.class, () -> categoryService.deleteCategory(2L));
      verify(categoryRepository, never()).delete(any(Category.class));
    }
  }
}
