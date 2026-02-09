package com.example.finance.controller;
import com.example.finance.dto.category.CategoryRequest;
import com.example.finance.dto.category.CategoryResponse;
import com.example.finance.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryService categoryService;
  public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
  }
  @GetMapping
  public ResponseEntity<Map<String, List<CategoryResponse>>> getAllCategories() {
    List<CategoryResponse> categories = categoryService.getAllCategories();
    Map<String, List<CategoryResponse>> response = new HashMap<>();
    response.put("categories", categories);
    return ResponseEntity.ok(response);
  }
  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @Valid @RequestBody CategoryRequest request) {
    CategoryResponse response = categoryService.createCategory(request);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }
  @DeleteMapping("/{name}")
  public ResponseEntity<Map<String, String>> deleteCategoryByName(@PathVariable String name) {
    categoryService.deleteCategoryByName(name);
    Map<String, String> response = new HashMap<>();
    response.put("message", "Category deleted successfully");
    return ResponseEntity.ok(response);
  }
}
