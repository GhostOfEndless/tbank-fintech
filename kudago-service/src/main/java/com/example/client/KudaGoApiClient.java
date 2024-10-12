package com.example.client;

import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@EnableRetry
@RequiredArgsConstructor
public class KudaGoApiClient {

    private final RestClient restClient;
    private final String kudaGoBaseUrl;

    @Retryable(
            maxAttemptsExpression = "${kudago.max-retries}",
            backoff = @Backoff(delayExpression = "${kudago.retry-delay}"),
            retryFor = {RuntimeException.class}
    )
    public List<CategoryPayload> fetchCategories() {
        return fetchData(kudaGoBaseUrl + "/place-categories/", CategoryPayload[].class);
    }

    @Retryable(
            maxAttemptsExpression = "${kudago.max-retries}",
            backoff = @Backoff(delayExpression = "${kudago.retry-delay}"),
            retryFor = {RuntimeException.class}
    )
    public List<LocationPayload> fetchLocations() {
        return fetchData(kudaGoBaseUrl + "/locations/", LocationPayload[].class);
    }

    private <T> List<T> fetchData(String uri, Class<T[]> responseType) {
        log.debug("Start request");
        ResponseEntity<T[]> response = restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(responseType);

        var body = response.getBody();

        if (body == null) {
            log.error("Received null response for URI: {}", uri);
            throw new RuntimeException();
        } else if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Unexpected HTTP status code '{}' for URI: {}", response.getStatusCode(), uri);
            throw new RuntimeException();
        }

        log.debug("Successfully fetched '{}' entries from '{}'", body.length, uri);
        return Arrays.asList(body);
    }
}
