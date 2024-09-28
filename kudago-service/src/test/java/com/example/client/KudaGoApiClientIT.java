package com.example.client;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@DisplayName("Fetch data from API")
@Tag("FetchData")
public class KudaGoApiClientIT {

    @Container
    static WireMockContainer wiremockServer = new WireMockContainer("wiremock/wiremock:2.35.1-1-alpine")
            .withMappingFromResource("locations", KudaGoApiClientIT.class, "locations.json")
            .withMappingFromResource("categories", KudaGoApiClientIT.class, "categories.json");

    @Autowired
    private KudaGoApiClient kudaGoApiClient;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("kudago-base-url", wiremockServer::getBaseUrl);
    }

    @Test
    @DisplayName("Should return list of all locations after fetch from API")
    public void testFetchLocations() {
        var locations = kudaGoApiClient.fetchLocations();
        assertThat(locations).isNotEmpty();
    }

    @Test
    @DisplayName("Should return list of all categories after fetch from API")
    public void testFetchCategories() {
        var categories = kudaGoApiClient.fetchCategories();
        assertThat(categories).isNotEmpty();
    }
}
