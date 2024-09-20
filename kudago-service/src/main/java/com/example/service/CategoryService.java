package com.example.service;

import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;

    @PostConstruct
    private void init() {
        log.info("LocationService initialization...");
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategoryById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    public Category createCategory(String slug, String name) {
        return repository.save(
                Category.builder()
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public Category updateCategory(Long id, String slug, String name) {
        return repository.save(
                Category.builder()
                        .id(id)
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public void deleteCategory(Long id) {
        repository.delete(id);
    }
}
