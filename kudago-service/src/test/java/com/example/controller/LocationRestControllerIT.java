package com.example.controller;

import com.example.BaseIT;
import com.example.controller.payload.LocationPayload;
import com.example.entity.Location;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LocationRestControllerIT extends BaseIT {

    private static final String uri = "/api/v1/locations";
    private static final Supplier<Stream<Arguments>> invalidLocationPayload = () -> Stream.of(
            Arguments.of(new LocationPayload(null, null)),
            Arguments.of(new LocationPayload("", "")),
            Arguments.of(new LocationPayload("    ", "    ")),
            Arguments.of(new LocationPayload("test", null)),
            Arguments.of(new LocationPayload("test", "")),
            Arguments.of(new LocationPayload("test", "    ")),
            Arguments.of(new LocationPayload(null, "test")),
            Arguments.of(new LocationPayload("", "test")),
            Arguments.of(new LocationPayload("    ", "test")),
            Arguments.of(new LocationPayload("a".repeat(6), "test")),
            Arguments.of(new LocationPayload("aa", "test")),
            Arguments.of(new LocationPayload("test", "a".repeat(51)))
    );

    @Nested
    @Tag("GetAll")
    @DisplayName("Get all locations")
    class GetAllLocationsTest {

        @SneakyThrows
        @Test
        @DisplayName("Should return empty list when no locations exist")
        public void getAllLocations_empty() {
            var mvcResponse = mockMvc.perform(get(uri))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var content = objectMapper.readValue(mvcResponse.getContentAsString(),
                    new TypeReference<List<Location>>() {
                    });

            assertThat(content).isEmpty();
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return list with one location when one location exists")
        public void getAllLocations_notEmpty() {
            var createdLocation = createLocation("test", "Test Location");

            var mvcResponse = mockMvc.perform(get(uri))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var locations = objectMapper.readValue(
                    mvcResponse.getContentAsString(),
                    new TypeReference<List<Location>>() {
                    });

            assertAll("Grouped assertions for existed location",
                    () -> assertEquals(locations.size(), 1),
                    () -> assertEquals(locations.getFirst(), createdLocation));

            deleteLocation(createdLocation.getId());
        }
    }

    @Nested
    @Tag("GetById")
    @DisplayName("Get location by id")
    class GetLocationByIdTest {

        @SneakyThrows
        @Test
        @DisplayName("Should return location when it exists")
        public void getLocationById_success() {
            var createdLocation = createLocation("test", "Test Location");

            var mvcResponse = mockMvc.perform(get(uri + "/" + createdLocation.getId()))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var actualLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

            assertThat(actualLocation).isEqualTo(createdLocation);

            deleteLocation(createdLocation.getId());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when location doesn't exist")
        public void getLocationById_notFound() {
            mockMvc.perform(get(uri + "/1"))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @Nested
    @Tag("Create")
    @DisplayName("Create location")
    class CreateLocationTest {

        @SneakyThrows
        @Test
        @DisplayName("Should create location with valid payload")
        public void createLocation_success() {
            var payload = LocationPayload.builder()
                    .slug("test")
                    .name("Test Location")
                    .build();

            var mvcResponse = mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpectAll(
                            status().isCreated(),
                            content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var actualLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

            assertAll("Grouped assertions for created location",
                    () -> assertEquals(actualLocation.getName(), payload.name()),
                    () -> assertEquals(actualLocation.getSlug(), payload.slug()));

            deleteLocation(actualLocation.getId());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("provideInvalidLocationPayload")
        @DisplayName("Should return 400 when payload is invalid")
        public void createLocation_badRequest(LocationPayload payload) {
            mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }

        private static Stream<Arguments> provideInvalidLocationPayload() {
            return invalidLocationPayload.get();
        }
    }

    @Nested
    @Tag("Update")
    @DisplayName("Update location")
    class UpdateLocationTest {

        @SneakyThrows
        @Test
        @DisplayName("Should update location when it exists")
        public void updateLocation_success() {
            var createdLocation = createLocation("old", "Old Location");

            var payload = LocationPayload.builder()
                    .slug("new")
                    .name("New Location")
                    .build();

            var mvcResponse = mockMvc.perform(put(uri + "/" + createdLocation.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpectAll(
                            status().isOk(),
                            content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var updatedLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

            assertAll("Grouped assertions for updated locations",
                    () -> assertEquals(updatedLocation.getId(), createdLocation.getId()),
                    () -> assertEquals(updatedLocation.getSlug(), payload.slug()),
                    () -> assertEquals(updatedLocation.getName(), payload.name())
            );

            deleteLocation(createdLocation.getId());
        }

        @SneakyThrows
        @ParameterizedTest
        @MethodSource("provideInvalidLocationPayload")
        @DisplayName("Should return 400 when update payload is invalid")
        public void updateLocation_badRequest(LocationPayload updatePayload) {
            var createdLocation = createLocation("old", "Old Name");

            mockMvc.perform(put(uri + "/" + createdLocation.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatePayload)))
                    .andExpectAll(
                            status().isBadRequest(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));

            deleteLocation(createdLocation.getId());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when updating non-existent location")
        public void updateLocation_notFound() {
            var payload = LocationPayload.builder()
                    .slug("new")
                    .name("New name")
                    .build();

            mockMvc.perform(put(uri + "/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }

        private static Stream<Arguments> provideInvalidLocationPayload() {
            return invalidLocationPayload.get();
        }
    }

    @Nested
    @Tag("Delete")
    @DisplayName("Delete location")
    class DeleteLocationTest {

        @SneakyThrows
        @Test
        @DisplayName("Should delete location when it exists")
        public void deleteLocation_success() {
            var createdLocation = createLocation("test", "Test Name");

            deleteLocation(createdLocation.getId());

            mockMvc.perform(get(uri + "/" + createdLocation.getId()))
                    .andExpect(status().isNotFound());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when deleting non-existent location")
        public void deleteLocation_notFound() {
            mockMvc.perform(delete(uri + "/999"))
                    .andExpectAll(
                            status().isNotFound(),
                            content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
        }
    }

    @SneakyThrows
    private Location createLocation(String slug, String name) {
        var payload = LocationPayload.builder()
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

        return objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);
    }

    @SneakyThrows
    private void deleteLocation(Long id) {
        mockMvc.perform(delete(uri + "/" + id))
                .andExpect(status().isNoContent());
    }
}