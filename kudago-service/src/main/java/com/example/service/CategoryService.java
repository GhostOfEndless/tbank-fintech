package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        repository.deleteAll();
        log.info("Fetching categories from API...");
        kudaGoApiClient.fetchCategories()
                .forEach(payload -> repository.save(
                        Category.builder()
                                .slug(payload.slug())
                                .name(payload.name())
                                .build()));
        log.info("Categories added!");
    }

    public List<Category> getAllCategories() {
        return repository.findAll();
    }

    public Category getCategoryById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("category.not_found"));
    }

    public Category createCategory(String slug, String name) {
        return repository.save(
                Category.builder()
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public Category updateCategory(Long id, String slug, String name) {
        if (repository.existsById(id)) {
            return repository.save(
                    Category.builder()
                            .id(id)
                            .slug(slug)
                            .name(name)
                            .build());
        }
        throw new NoSuchElementException("category.not_found");
    }

    public void deleteCategory(Long id) {
        if (repository.existsById(id)) {
            repository.delete(id);
            return;
        }
        throw new NoSuchElementException("category.not_found");
    }
}
