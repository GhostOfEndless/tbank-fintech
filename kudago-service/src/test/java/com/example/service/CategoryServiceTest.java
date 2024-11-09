package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.controller.payload.CategoryPayload;
import com.example.entity.Category;
import com.example.exception.entity.CategoryNotFoundException;
import com.example.repository.CategoryRepository;
import com.example.service.observer.category.CategoryPublisher;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

  @SuppressWarnings("unused")
  @Mock
  CategoryPublisher categoryPublisher;

  @Mock
  CategoryRepository repository;

  @Mock
  KudaGoApiClient kudaGoApiClient;

  @InjectMocks
  CategoryService service;

  private final Category category = Category.builder()
      .id(1L)
      .name("test")
      .slug("test")
      .build();

  @Nested
  @Tag("GetAllCategories")
  @DisplayName("Get all categories tests")
  class GetAllCategoriesTest {

    @Test
    @DisplayName("Should return empty list when no categories exist")
    public void getAllCategories_empty() {
      doReturn(List.of()).when(repository).findAll();

      var categories = service.getAllCategories();

      assertThat(categories).isEmpty();
    }

    @Test
    @DisplayName("Should return list of categories when categories exist")
    public void getAllCategories_notEmpty() {
      doReturn(List.of(category)).when(repository).findAll();

      var categories = service.getAllCategories();

      assertThat(categories).contains(category);
    }
  }

  @Nested
  @Tag("GetById")
  @DisplayName("Get category by ID tests")
  class GetCategoryByIdTest {

    @Test
    @DisplayName("Should return category when found by ID")
    public void getById_success() {
      doReturn(Optional.of(category)).when(repository).findById(category.getId());

      var receivedCategory = service.getCategoryById(category.getId());

      assertThat(receivedCategory).isEqualTo(category);
    }

    @Test
    @DisplayName("Should throw exception when category not found by ID")
    public void getById_notFound() {
      assertThatExceptionOfType(CategoryNotFoundException.class)
          .isThrownBy(() -> service.getCategoryById(category.getId()))
          .withMessage("category.not_found");
    }
  }

  @Test
  @Tag("Create")
  @DisplayName("Should successfully create a new category")
  public void createCategory_success() {
    var payloadCategory = Category.builder()
        .slug(category.getSlug())
        .name(category.getName())
        .build();
    doReturn(category).when(repository).save(payloadCategory);
    doReturn(false).when(repository).existsBySlug(payloadCategory.getSlug());

    var savedCategory = service.createCategory(payloadCategory.getSlug(), payloadCategory.getName());

    assertThat(savedCategory).isEqualTo(category);
    verify(repository).save(payloadCategory);
    verify(repository).existsBySlug(payloadCategory.getSlug());
    verifyNoMoreInteractions(repository);
  }


  @Nested
  @Tag("Update")
  @DisplayName("Update category tests")
  class UpdateCategoryTest {

    @Test
    @DisplayName("Should successfully update an existing category")
    public void updateCategory_success() {
      var changedCategory = Category.builder()
          .name(category.getName() + "-updated")
          .slug(category.getSlug())
          .id(category.getId())
          .build();
      doReturn(Optional.of(category)).when(repository).findById(changedCategory.getId());
      doReturn(false).when(repository).existsBySlug(changedCategory.getSlug());
      doReturn(changedCategory).when(repository).save(changedCategory);

      var updatedCategory = service.updateCategory(category.getId(),
          changedCategory.getSlug(), changedCategory.getName());

      assertThat(changedCategory).isEqualTo(updatedCategory);
      verify(repository).save(changedCategory);
      verify(repository).existsBySlug(changedCategory.getSlug());
      verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent category")
    public void updateCategory_notFound() {
      var changedCategory = Category.builder()
          .name(category.getName() + "-updated")
          .slug(category.getSlug())
          .id(category.getId())
          .build();
      doReturn(Optional.empty()).when(repository).findById(changedCategory.getId());

      assertThatExceptionOfType(CategoryNotFoundException.class)
          .isThrownBy(() -> service.updateCategory(
              category.getId(),
              changedCategory.getSlug(),
              changedCategory.getName()))
          .withMessage("category.not_found");
    }
  }

  @Nested
  @Tag("Delete")
  @DisplayName("Delete category tests")
  class DeleteCategory {

    @Test
    @DisplayName("Should successfully delete an existing category")
    public void deleteCategory_success() {
      doReturn(Optional.of(category)).when(repository).findById(category.getId());

      service.deleteCategory(category.getId());

      verify(repository).deleteById(category.getId());
      verifyNoMoreInteractions(repository);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent category")
    public void deleteCategory_notFound() {
      doReturn(Optional.empty()).when(repository).findById(category.getId());

      assertThatExceptionOfType(CategoryNotFoundException.class)
          .isThrownBy(() -> service.deleteCategory(category.getId()))
          .withMessage("category.not_found");
    }
  }

  @Test
  @Tag("Init")
  @DisplayName("Should fetch categories from API during initialization")
  public void init_invokesApi() {
    var payload = CategoryPayload.builder()
        .name("test")
        .slug("test")
        .build();

    var categoryForSave = Category.builder()
        .slug(payload.slug())
        .name(payload.name())
        .build();

    var savedCategory = Category.builder()
        .id(1L)
        .slug(payload.slug())
        .name(payload.name())
        .build();

    doReturn(List.of(payload)).when(kudaGoApiClient).fetchCategories();
    doReturn(savedCategory).when(repository).save(categoryForSave);

    service.init();

    verify(kudaGoApiClient).fetchCategories();
    verifyNoMoreInteractions(kudaGoApiClient);
  }
}
