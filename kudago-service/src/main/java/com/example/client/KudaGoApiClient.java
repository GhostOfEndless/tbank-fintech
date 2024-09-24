package com.example.client;

import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KudaGoApiClient {

    private final RestClient restClient;

    public List<CategoryPayload> fetchCategories() {
        return fetchData("https://kudago.com/public-api/v1.4/place-categories/", CategoryPayload[].class);
    }

    public List<LocationPayload> fetchLocations() {
        return fetchData("https://kudago.com/public-api/v1.4/locations/", LocationPayload[].class);
    }

    @Retryable(
            maxAttemptsExpression = "${kudago.max-retries}",
            backoff = @Backoff(delayExpression = "${kudago.retry-delay}"),
            retryFor = RuntimeException.class
    )
    private <T> List<T> fetchData(String uri, Class<T[]> responseType) {
        ResponseEntity<T[]> response = restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(responseType);

        var body = response.getBody();

        if (body == null) {
            log.error("Received null response for URI: {}", uri);
            throw new RuntimeException("Null response received");
        } else if (!response.getStatusCode().is2xxSuccessful()) {
            log.error("Unexpected HTTP status code '{}' for URI: {}", response.getStatusCode(), uri);
            throw new RuntimeException("Unexpected HTTP status code");
        }

        log.debug("Successfully fetched '{}' entries from '{}'", body.length, uri);
        return Arrays.asList(body);
    }
}
