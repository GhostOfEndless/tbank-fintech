package com.example.client;

import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import com.example.entity.Event;
import com.example.entity.EventsResponse;
import com.example.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.lang.reflect.Array;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@EnableRetry
@RequiredArgsConstructor
public class KudaGoApiClient {

    private final WebClient kudaGoWebClient;

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

    public Mono<List<Event>> getEventsReactive(LocalDate dateFrom, LocalDate dateTo) {
        return Flux.range(1, Integer.MAX_VALUE)
                .concatMap(page -> getEventsFromPageReactive(dateFrom, dateTo, page))
                .takeWhile(eventsResponse -> !eventsResponse.getResults().isEmpty())
                .flatMapIterable(EventsResponse::getResults)
                .collectList();
    }

    public CompletableFuture<List<Event>> getEventsFuture(LocalDate dateFrom, LocalDate dateTo) {
        return CompletableFuture.supplyAsync(() -> {
            List<Event> allEvents = new ArrayList<>();
            int page = 1;
            while (true) {
                EventsResponse eventsResponse = getEventsFromPageFuture(dateFrom, dateTo, page).join();
                if (eventsResponse.getResults().isEmpty()) {
                    break;
                }
                allEvents.addAll(eventsResponse.getResults());
                page++;
            }
            return allEvents;
        });
    }

    private @NotNull <T> List<T> fetchData(String uri, Class<T[]> responseType) {
        log.debug("Start request");
        var response = kudaGoWebClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(responseType)
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(1000)))
                .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
                .onErrorResume(error -> Mono.just(responseType.cast( // return empty array
                        Array.newInstance(responseType.getComponentType(), 0))))
                .block();

        log.debug("Successfully fetched '{}' entries from '{}'", Objects.requireNonNull(response).length, uri);
        return Arrays.asList(response);
    }

    private Mono<EventsResponse> getEventsFromPageReactive(LocalDate dateFrom, LocalDate dateTo, int page) {
        return getEventsFromPage(dateFrom, dateTo, page)
                .onErrorResume(WebClientResponseException.NotFound.class,
                        error -> Mono.just(new EventsResponse()))
                .onErrorResume(WebClientResponseException.ServiceUnavailable.class,
                        error -> Mono.error(new ServiceUnavailableException()))
                .onErrorResume(WebClientRequestException.class,
                        error -> Mono.error(new ServiceUnavailableException()));
    }

    private CompletableFuture<EventsResponse> getEventsFromPageFuture(LocalDate dateFrom, LocalDate dateTo, int page) {
        return getEventsFromPage(dateFrom, dateTo, page)
                .toFuture()
                .exceptionally(throwable -> {
                    if (throwable instanceof WebClientResponseException.NotFound) {
                        return new EventsResponse();
                    }
                    log.error("Error occurred while data fetch from API");
                    throw new ServiceUnavailableException();
                });
    }

    private Mono<EventsResponse> getEventsFromPage(LocalDate dateFrom, LocalDate dateTo, int page) {
        return kudaGoWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/events/")
                        .queryParam("actual_since", dateFrom.toString())
                        .queryParam("actual_until", dateTo.toString())
                        .queryParam("page", page)
                        .queryParam("order_by", "is_free,price")
                        .queryParam("text_format", "text")
                        .queryParam("location", "msk")
                        .queryParam("fields", "id,title,price,is_free")
                        .build())
                .retrieve()
                .bodyToMono(EventsResponse.class);
    }
}
