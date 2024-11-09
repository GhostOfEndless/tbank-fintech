package com.example.controller;

import com.example.BaseIT;
import com.example.controller.payload.CategoryPayload;
import com.example.entity.Category;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryRestControllerIT extends BaseIT {

  private static final String uri = "/api/v1/places/categories";
  private static final Supplier<Stream<Arguments>> invalidCategoryPayload = () -> Stream.of(
      Arguments.of(new CategoryPayload(null, null)),
      Arguments.of(new CategoryPayload("", "")),
      Arguments.of(new CategoryPayload("    ", "    ")),
      Arguments.of(new CategoryPayload("test", null)),
      Arguments.of(new CategoryPayload("test", "")),
      Arguments.of(new CategoryPayload("test", "    ")),
      Arguments.of(new CategoryPayload(null, "test")),
      Arguments.of(new CategoryPayload("", "test")),
      Arguments.of(new CategoryPayload("    ", "test")),
      Arguments.of(new CategoryPayload("a".repeat(26), "test")),
      Arguments.of(new CategoryPayload("aa", "test")),
      Arguments.of(new CategoryPayload("test", "a".repeat(51)))
  );

  @SneakyThrows
  private Category createCategory(String slug, String name) {
    var payload = CategoryPayload.builder()
        .slug(slug)
        .name(name)
        .build();

    var mvcResponse = mockMvc.perform(post(uri)
            .header("Authorization", adminBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpectAll(
            status().isCreated(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    return objectMapper.readValue(mvcResponse.getContentAsString(), Category.class);
  }

  @SneakyThrows
  private void deleteCategory(Long id) {
    mockMvc.perform(delete(uri + "/" + id)
            .header("Authorization", adminBearerToken))
        .andExpect(status().isNoContent());
  }

  @Nested
  @Tag("GetAll")
  @DisplayName("Get all categories")
  class GetAllCategoriesTest {

    @SneakyThrows
    @Test
    @DisplayName("Should return empty list when no categories exist")
    public void getAllCategories_empty() {
      var mvcResponse = mockMvc.perform(get(uri)
              .header("Authorization", userBearerToken))
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON))
          .andReturn()
          .getResponse();

      var content = objectMapper.readValue(mvcResponse.getContentAsString(),
          new TypeReference<List<Category>>() {
          });

      assertThat(content).isEmpty();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return list with one category when one category exists")
    public void getAllCategories_notEmpty() {
      var createdCategory = createCategory("test", "Test Category");

      var mvcResponse = mockMvc.perform(get(uri)
              .header("Authorization", adminBearerToken))
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON))
          .andReturn()
          .getResponse();

      var categories = objectMapper.readValue(
          mvcResponse.getContentAsString(),
          new TypeReference<List<Category>>() {
          });

      assertAll("Grouper assertions for existed category",
          () -> assertEquals(categories.size(), 1),
          () -> assertEquals(categories.getFirst(), createdCategory));

      deleteCategory(createdCategory.getId());
    }
  }

  @Nested
  @Tag("GetById")
  @DisplayName("Get category by id")
  class GetCategoryByIdTest {

    @SneakyThrows
    @Test
    @DisplayName("Should return category when it exists")
    public void getCategoryById_success() {
      var createdCategory = createCategory("test", "Test Category");

      var mvcResponse = mockMvc.perform(get(uri + "/" + createdCategory.getId())
              .header("Authorization", adminBearerToken))
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON))
          .andReturn()
          .getResponse();

      var actualCategory = objectMapper.readValue(mvcResponse.getContentAsString(), Category.class);

      assertThat(actualCategory).isEqualTo(createdCategory);

      deleteCategory(createdCategory.getId());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 404 when category doesn't exist")
    public void getCategoryById_notFound() {
      mockMvc.perform(get(uri + "/1")
              .header("Authorization", userBearerToken))
          .andExpectAll(
              status().isNotFound(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
  }

  @Nested
  @Tag("Create")
  @DisplayName("Create category")
  class CreateCategoryTest {

    public static Stream<Arguments> provideInvalidCategoryPayload() {
      return invalidCategoryPayload.get();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should create category with valid payload")
    public void createCategory_success() {
      var payload = CategoryPayload.builder()
          .slug("test")
          .name("Test Category")
          .build();

      var mvcResponse = mockMvc.perform(post(uri)
              .header("Authorization", adminBearerToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(payload)))
          .andExpectAll(
              status().isCreated(),
              content().contentType(MediaType.APPLICATION_JSON))
          .andReturn()
          .getResponse();

      var actualCategory = objectMapper.readValue(mvcResponse.getContentAsString(), Category.class);

      assertAll("Grouped assertions for created category",
          () -> assertEquals(actualCategory.getSlug(), payload.slug()),
          () -> assertEquals(actualCategory.getName(), payload.name()));

      deleteCategory(actualCategory.getId());
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return 400 when payload is invalid")
    @MethodSource("provideInvalidCategoryPayload")
    public void createCategory_badRequest(CategoryPayload payload) {
      mockMvc.perform(post(uri)
              .header("Authorization", userBearerToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(payload)))
          .andExpectAll(
              status().isBadRequest(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
  }

  @Nested
  @Tag("Update")
  @DisplayName("Update category")
  class UpdateCategoryTest {

    public static Stream<Arguments> provideInvalidCategoryPayload() {
      return invalidCategoryPayload.get();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should update category when it exists")
    public void updateCategory_success() {
      var createdCategory = createCategory("old", "Old Name");

      var payload = CategoryPayload.builder()
          .slug("new")
          .name("New Name")
          .build();

      var mvcResponse = mockMvc.perform(put(uri + "/" + createdCategory.getId())
              .header("Authorization", adminBearerToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(payload)))
          .andExpectAll(
              status().isOk(),
              content().contentType(MediaType.APPLICATION_JSON))
          .andReturn()
          .getResponse();

      var updatedCategory = objectMapper.readValue(mvcResponse.getContentAsString(), Category.class);

      assertAll("Grouped assertions for updated category",
          () -> assertEquals(updatedCategory.getId(), createdCategory.getId()),
          () -> assertEquals(updatedCategory.getName(), payload.name()),
          () -> assertEquals(updatedCategory.getSlug(), payload.slug()));

      deleteCategory(createdCategory.getId());
    }

    @SneakyThrows
    @ParameterizedTest
    @DisplayName("Should return 400 when update payload is invalid")
    @MethodSource("provideInvalidCategoryPayload")
    public void updateCategory_badRequest(CategoryPayload updatePayload) {
      var createdCategory = createCategory("old", "Old Name");

      mockMvc.perform(put(uri + "/" + createdCategory.getId())
              .header("Authorization", userBearerToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatePayload)))
          .andExpectAll(
              status().isBadRequest(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

      deleteCategory(createdCategory.getId());
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 404 when updating non-existent category")
    public void updateCategory_notFound() {
      var updatePayload = CategoryPayload.builder()
          .slug("new")
          .name("New name")
          .build();

      mockMvc.perform(put(uri + "/1")
              .header("Authorization", adminBearerToken)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updatePayload)))
          .andExpectAll(
              status().isNotFound(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
  }

  @Nested
  @Tag("Delete")
  @DisplayName("Delete category")
  class DeleteCategoryTest {

    @SneakyThrows
    @Test
    @DisplayName("Should delete category when it exists")
    public void deleteCategory_success() {
      var createdCategory = createCategory("test", "Test Category");

      deleteCategory(createdCategory.getId());

      mockMvc.perform(get(uri + "/" + createdCategory.getId())
              .header("Authorization", adminBearerToken))
          .andExpectAll(
              status().isNotFound(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 404 when deleting non-existent category")
    public void deleteCategory_notFound() {
      mockMvc.perform(delete(uri + "/1")
              .header("Authorization", adminBearerToken))
          .andExpectAll(
              status().isNotFound(),
              content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
    }
  }
}
