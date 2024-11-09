package com.example.controller;

import com.example.aspect.LogExecutionTime;
import com.example.controller.payload.CategoryPayload;
import com.example.entity.Category;
import com.example.entity.history.CategoryMemento;
import com.example.service.CategoryService;
import com.example.service.history.CategoryHistoryCaretaker;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1/places/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryService categoryService;
    private final CategoryHistoryCaretaker historyCaretaker;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Category getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("/{id}/history")
    public List<CategoryMemento> getHistory(@PathVariable Long id) {
        return historyCaretaker.getHistory(id);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody CategoryPayload category) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.createCategory(category.slug(), category.name()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Category updateCategory(@PathVariable Long id,
                                   @Valid @RequestBody CategoryPayload category) {
        return categoryService.updateCategory(id, category.slug(), category.name());
    }

    @DeleteMapping("/{id}")
     @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
