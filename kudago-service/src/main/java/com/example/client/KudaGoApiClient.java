package com.example.client;

import com.example.config.KudaGoClientProperties;
import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class KudaGoApiClient {

    private final KudaGoClientProperties properties;
    private final RestTemplate restTemplate;

    public List<CategoryPayload> fetchCategories() {
        return fetchDataWithRetry("https://kudago.com/public-api/v1.4/place-categories", CategoryPayload[].class);
    }

    public List<LocationPayload> fetchLocations() {
        return fetchDataWithRetry("https://kudago.com/public-api/v1.4/locations", LocationPayload[].class);
    }

    private <T> List<T> fetchDataWithRetry(String url, Class<T[]> responseType) {
        return CompletableFuture.supplyAsync((Supplier<List<T>>) () -> {
            for (int attempt = 0; attempt < properties.getMaxRetries(); attempt++) {
                try {
                    T[] response = restTemplate.getForObject(url, responseType);
                    if (response == null) {
                        return Collections.emptyList();
                    }
                    log.debug("Successfully fetched '{}' entries!", response.length);
                    return Arrays.asList(response);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    try {
                        //noinspection BusyWait
                        Thread.sleep(properties.getRetryDelay());
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Thread interrupted during retry wait", ie);
                    }
                }
            }

            log.error("Failed to fetch data after {} attempts", properties.getMaxRetries());
            return Collections.emptyList();
        }).join();
    }
}
