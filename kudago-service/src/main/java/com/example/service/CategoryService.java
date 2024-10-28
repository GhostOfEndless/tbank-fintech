package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Category;
import com.example.entity.history.CategoryMemento;
import com.example.entity.CrudAction;
import com.example.exception.entity.CategoryNotFoundException;
import com.example.repository.CategoryRepository;
import com.example.service.history.CategoryHistoryCaretaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryHistoryCaretaker historyCaretaker;
    private final CategoryRepository categoryRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching categories from API...");
        kudaGoApiClient.fetchCategories()
                .forEach(payload -> {
                    if (!categoryRepository.existsBySlug(payload.slug())) {
                        var category = categoryRepository.save(
                                Category.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());
                        historyCaretaker.saveSnapshot(CategoryMemento.createSnapshot(category, CrudAction.CREATE));
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
        var category = categoryRepository.save(
                Category.builder()
                        .slug(slug)
                        .name(name)
                        .build());
        historyCaretaker.saveSnapshot(CategoryMemento.createSnapshot(category, CrudAction.CREATE));

        return category;
    }

    public Category updateCategory(Long id, String slug, String name) {
        var category = getCategoryById(id);

        category.setName(name);
        category.setSlug(slug);

        var updatedCategory =  categoryRepository.save(category);
        historyCaretaker.saveSnapshot(CategoryMemento.createSnapshot(category, CrudAction.UPDATE));

        return updatedCategory;
    }

    public void deleteCategory(Long id) {
        var category = getCategoryById(id);
        categoryRepository.deleteById(id);
        historyCaretaker.saveSnapshot(CategoryMemento.createSnapshot(category, CrudAction.DELETE));
    }
}
