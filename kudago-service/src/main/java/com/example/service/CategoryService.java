package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Category;
import com.example.exception.entity.CategoryNotFoundException;
import com.example.repository.jpa.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching categories from API...");
        kudaGoApiClient.fetchCategories()
                .forEach(payload -> {
                    if (!categoryRepository.existsBySlug(payload.slug())) {
                        categoryRepository.save(
                                Category.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());
                    }
                });
        log.info("Categories added!");
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() ->
                new CategoryNotFoundException(id));
    }

    public Category createCategory(String slug, String name) {
        return categoryRepository.save(
                Category.builder()
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public Category updateCategory(Long id, String slug, String name) {
        var category = getCategoryById(id);

        category.setName(name);
        category.setSlug(slug);

        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        getCategoryById(id);
        categoryRepository.deleteById(id);
    }
}
