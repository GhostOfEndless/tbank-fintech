package com.example.controller;

import com.example.BaseIT;
import com.example.controller.payload.CategoryPayload;
import com.example.entity.Category;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CategoryRestControllerIT extends BaseIT {

    private final String uri = "/api/v1/places/categories";

    @Nested
    @Tag("GetAll")
    @DisplayName("Get all categories")
    class GetAllCategoriesTest {

        @SneakyThrows
        @Test
        @DisplayName("Should return empty list when no categories exist")
        public void getAllCategories_empty() {
            var mvcResponse = mockMvc.perform(get(uri))
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

            var mvcResponse = mockMvc.perform(get(uri))
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

            var mvcResponse = mockMvc.perform(get(uri + "/" + createdCategory.getId()))
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
            mockMvc.perform(get(uri + "/1"))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @Nested
    @Tag("Create")
    @DisplayName("Create category")
    class CreateCategoryTest {

        @SneakyThrows
        @Test
        @DisplayName("Should create category with valid payload")
        public void createCategory_success() {
            var payload = CategoryPayload.builder()
                    .slug("test")
                    .name("Test Category")
                    .build();

            var mvcResponse = mockMvc.perform(post(uri)
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
        @Test
        @DisplayName("Should return 400 when payload is invalid")
        public void createCategory_badRequest() {
            var payload = CategoryPayload.builder()
                    .slug(" ")
                    .build();

            mockMvc.perform(post(uri)
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
        @Test
        @DisplayName("Should return 400 when update payload is invalid")
        public void updateCategory_badRequest() {
            var createdCategory = createCategory("old", "Old Name");

            var updatePayload = CategoryPayload.builder()
                    .slug(" ")
                    .build();

            mockMvc.perform(put(uri + "/" + createdCategory.getId())
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

            mockMvc.perform(get(uri + "/" + createdCategory.getId()))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when deleting non-existent category")
        public void deleteCategory_notFound() {
            mockMvc.perform(delete(uri + "/1"))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @SneakyThrows
    private Category createCategory(String slug, String name) {
        var payload = CategoryPayload.builder()
                .slug(slug)
                .name(name)
                .build();

        var mvcResponse = mockMvc.perform(post(uri)
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
        mockMvc.perform(delete(uri + "/" + id))
                .andExpect(status().isNoContent());
    }
}
