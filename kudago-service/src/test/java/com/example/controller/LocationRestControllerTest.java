package com.example.controller;

import com.example.BaseTest;
import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import com.example.entity.Location;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@RequiredArgsConstructor
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LocationRestControllerTest extends BaseTest {

    private final MockMvc mockMvc;
    private final String uri = "/api/v1/locations";

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void init() {
        objectMapper = new ObjectMapper();
    }

    @Nested
    @Tag("GetAll")
    @DisplayName("Get all locations")
    class GetAllLocationsTest {

        @SneakyThrows
        @Test
        @DisplayName("Should return empty list when no locations exist")
        public void getAllLocations_empty() {
            var mvcResponse = mockMvc.perform(get(uri))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
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
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var locations = objectMapper.readValue(
                    mvcResponse.getContentAsString(),
                    new TypeReference<List<Location>>() {
                    });

            assertThat(locations).hasSize(1);
            assertThat(locations.getFirst()).isEqualTo(createdLocation);
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
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var actualLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);
            assertThat(actualLocation).isEqualTo(createdLocation);
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when location doesn't exist")
        public void getLocationById_notFound() {
            mockMvc.perform(get(uri + "/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
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
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var actualLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

            assertThat(actualLocation.getName()).isEqualTo(payload.name());
            assertThat(actualLocation.getSlug()).isEqualTo(payload.slug());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 400 when payload is invalid")
        public void createLocation_badRequest() {
            var payload = LocationPayload.builder()
                    .slug(" ")
                    .build();

            mockMvc.perform(post(uri)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
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
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse();

            var updatedLocation = objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);

            assertThat(updatedLocation.getId()).isEqualTo(createdLocation.getId());
            assertThat(updatedLocation.getSlug()).isEqualTo(payload.slug());
            assertThat(updatedLocation.getName()).isEqualTo(payload.name());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 400 when update payload is invalid")
        public void updateLocation_badRequest() {
            var createdLocation = createLocation("old", "Old Name");

            var payload = CategoryPayload.builder()
                    .slug(" ")
                    .build();

            mockMvc.perform(put(uri + "/" + createdLocation.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
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
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
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

            mockMvc.perform(delete(uri + "/" + createdLocation.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(uri + "/" + createdLocation.getId()))
                    .andExpect(status().isNotFound());
        }

        @SneakyThrows
        @Test
        @DisplayName("Should return 404 when deleting non-existent location")
        public void deleteLocation_notFound() {
            mockMvc.perform(delete(uri + "/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON));
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
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        return objectMapper.readValue(mvcResponse.getContentAsString(), Location.class);
    }
}