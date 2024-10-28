package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Category;
import com.example.entity.CrudAction;
import com.example.exception.entity.CategoryExistsException;
import com.example.exception.entity.CategoryNotFoundException;
import com.example.repository.CategoryRepository;
import com.example.service.observer.category.CategoryPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryPublisher categoryPublisher;
    private final CategoryRepository categoryRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching categories from API...");
        kudaGoApiClient.fetchCategories()
                .forEach(payload -> {
                    if (!categoryRepository.existsBySlug(payload.slug())) {
                        var savedCategory = categoryRepository.save(
                                Category.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());
                        categoryPublisher.notifyObservers(savedCategory, CrudAction.CREATE);
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
        if (categoryRepository.existsBySlug(slug)) {
            throw new CategoryExistsException(slug);
        }

        var savedCategory = categoryRepository.save(
                Category.builder()
                        .slug(slug)
                        .name(name)
                        .build());
        categoryPublisher.notifyObservers(savedCategory, CrudAction.CREATE);

        return savedCategory;
    }

    public Category updateCategory(Long id, String slug, String name) {
        var oldCategory = getCategoryById(id);

        if (categoryRepository.existsBySlug(slug)) {
            throw new CategoryExistsException(slug);
        }

        oldCategory.setName(name);
        oldCategory.setSlug(slug);

        var updatedCategory =  categoryRepository.save(oldCategory);
        categoryPublisher.notifyObservers(updatedCategory, CrudAction.UPDATE);

        return updatedCategory;
    }

    public void deleteCategory(Long id) {
        var category = getCategoryById(id);
        categoryRepository.deleteById(id);
        categoryPublisher.notifyObservers(category, CrudAction.DELETE);
    }
}
