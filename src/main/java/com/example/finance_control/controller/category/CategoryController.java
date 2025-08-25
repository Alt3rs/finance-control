package com.example.finance_control.controller.category;

import com.example.finance_control.domain.category.Category;
import com.example.finance_control.domain.type.Type;
import com.example.finance_control.dto.CategoryResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        List<CategoryResponseDTO> categories = Arrays.stream(Category.values())
                .map(CategoryResponseDTO::new)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByType(@PathVariable Type type) {
        Category[] categories = type == Type.REVENUE ?
                Category.getRevenueCategories() : Category.getExpenseCategories();

        List<CategoryResponseDTO> response = Arrays.stream(categories)
                .map(CategoryResponseDTO::new)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{categoryName}")
    public ResponseEntity<CategoryResponseDTO> getCategoryDetails(@PathVariable String categoryName) {
        try {
            Category category = Category.valueOf(categoryName.toUpperCase());
            return ResponseEntity.ok(new CategoryResponseDTO(category));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
