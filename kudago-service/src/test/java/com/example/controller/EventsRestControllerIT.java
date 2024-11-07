package com.example.controller;

import com.example.BaseIT;
import com.example.client.dto.EventResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@Testcontainers
public class EventsRestControllerIT extends BaseIT {

    private static final String uri = "/api/v1/events";

    private static final Supplier<Stream<Arguments>> invalidQueryParams = () -> Stream.of(
            // budget, currency, dateFrom, dateTo
            Arguments.of("", "", "", ""),
            Arguments.of("-0.1", "usd", "", ""),
            Arguments.of("", "usd", "", ""),
            Arguments.of("0,1", "usd", "", ""),
            Arguments.of("10.0", "ugf", "", ""),
            Arguments.of("10.0", "43h", "", ""),
            Arguments.of("10.0", "", "", ""),
            Arguments.of("10.0", "usd", "2024-10-10", "2024-09-10"),
            Arguments.of("10.0", "usd", "fds", "2024-10"),
            Arguments.of("10.0", "usd", "2024-10-10", ""),
            Arguments.of("10.0", "usd", "", "2024-09-10")
    );

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(@NotNull DynamicPropertyRegistry registry) {
        registry.add("kudago.base-url", wireMockServer::baseUrl);
        registry.add("currency-service.base-url", wireMockServer::baseUrl);
    }

    private static Stream<Arguments> getInvalidResponses() {
        return invalidQueryParams.get();
    }

    @AfterEach
    void destroy() {
        wireMockServer.resetAll();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return non empty result for CompletableFuture realisation")
    void getEventsFuture() {
        registerStubs();

        var mvcResponse = getResponse("/future", 200, MediaType.APPLICATION_JSON);

        var content = objectMapper.readValue(mvcResponse.getContentAsString(),
                new TypeReference<List<EventResponse>>() {
                });

        assertThat(content).isNotEmpty();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return non empty result for Project Reactor realisation")
    void getEventsReactive() {
        registerStubs();

        var mvcResponse = getResponse("/reactive", 200, MediaType.APPLICATION_JSON);

        var content = objectMapper.readValue(mvcResponse.getContentAsString(),
                new TypeReference<List<EventResponse>>() {
                });

        assertThat(content).isNotEmpty();
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 503 status when KudaGo API is unavailable for Project Reactor realisation")
    void getEventsReactive_error503() {
        registerStubs503();
        getResponse("/reactive", 503, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 503 status when KudaGo API is unavailable for CompletableFuture realisation")
    void getEventsFuture_error503() {
        registerStubs503();
        getResponse("/future", 503, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 503 status when connection error for Project Reactor realisation")
    void getEventsReactive_connectionError() {
        registerStubsConnectionError();
        getResponse("/reactive", 503, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @SneakyThrows
    @Test
    @DisplayName("Should return 503 status when connection error for CompletableFuture realisation")
    void getEventsFuture_connectionError() {
        registerStubsConnectionError();
        getResponse("/future", 503, MediaType.APPLICATION_PROBLEM_JSON);
    }

    @ParameterizedTest
    @SneakyThrows
    @MethodSource("getInvalidResponses")
    @DisplayName("Should return 400 status while request is invalid")
    void getEventsReactive_badRequest(String budget, String currency, String dateFrom, String dateTo) {
        registerStubs();

        mockMvc.perform(MockMvcRequestBuilders.get(uri + "/reactive")
                        .header("Authorization", bearerToken)
                        .param("currency", currency)
                        .param("budget", budget)
                        .param("dateFrom", dateFrom)
                        .param("dateTo", dateTo))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andReturn()
                .getResponse();
    }

    @SneakyThrows
    private MockHttpServletResponse getResponse(String url, int status, MediaType mediaType) {
        return mockMvc.perform(MockMvcRequestBuilders.get(uri + url)
                        .header("Authorization", bearerToken)
                        .param("currency", "usd")
                        .param("budget", "20.0"))
                .andExpectAll(
                        status().is(status),
                        content().contentType(mediaType))
                .andReturn()
                .getResponse();
    }

    private void registerStubs() {
        wireMockServer.stubFor(post(urlEqualTo("/convert"))
                .willReturn(aResponse().proxiedFrom("http://localhost:8081/api/v1/currencies")));

        wireMockServer.stubFor(get(urlPathEqualTo("/events/"))
                .willReturn(aResponse().proxiedFrom("https://kudago.com/public-api/v1.4")));
    }

    private void registerStubs503() {
        wireMockServer.stubFor(post(urlEqualTo("/convert"))
                .willReturn(serviceUnavailable()));

        wireMockServer.stubFor(get(urlPathEqualTo("/events/"))
                .willReturn(serviceUnavailable()));
    }

    private void registerStubsConnectionError() {
        wireMockServer.stubFor(post(urlEqualTo("/convert"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        wireMockServer.stubFor(get(urlPathEqualTo("/events/"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));
    }
}
