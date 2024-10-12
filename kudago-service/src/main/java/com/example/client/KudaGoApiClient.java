package com.example.client;

import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@EnableRetry
@RequiredArgsConstructor
public class KudaGoApiClient {

    private final WebClient webClient;

    @Retryable(
            maxAttemptsExpression = "${kudago.max-retries}",
            backoff = @Backoff(delayExpression = "${kudago.retry-delay}"),
            retryFor = {RuntimeException.class}
    )
    public List<CategoryPayload> fetchCategories() {
        return fetchData("/place-categories/", CategoryPayload[].class);
    }

    @Retryable(
            maxAttemptsExpression = "${kudago.max-retries}",
            backoff = @Backoff(delayExpression = "${kudago.retry-delay}"),
            retryFor = {RuntimeException.class}
    )
    public List<LocationPayload> fetchLocations() {
        return fetchData("/locations/", LocationPayload[].class);
    }

    private <T> List<T> fetchData(String uri, Class<T[]> responseType) {
        log.debug("Start request");
        var response = webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(1000)))
                .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(responseType.cast( // return empty array
                        Array.newInstance(responseType.getComponentType(), 0))))
                .block();

        if (response == null) {
            log.error("Received null response for URI: {}", uri);
            throw new RuntimeException();
        }

        log.debug("Successfully fetched '{}' entries from '{}'", response.length, uri);
        return Arrays.asList(response);
    }
}
