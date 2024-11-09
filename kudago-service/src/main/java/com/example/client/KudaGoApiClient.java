package com.example.client;

import com.example.client.dto.EventResponse;
import com.example.client.dto.EventsResponse;
import com.example.config.KudaGoClientProperties;
import com.example.controller.payload.CategoryPayload;
import com.example.controller.payload.LocationPayload;
import com.example.exception.ServiceUnavailableException;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Component
@EnableRetry
@RequiredArgsConstructor
public class KudaGoApiClient {

  private final WebClient kudaGoWebClient;
  private final KudaGoClientProperties properties;

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

  public Mono<List<EventResponse>> getEventsReactive(LocalDate dateFrom, LocalDate dateTo, String location) {
    return Flux.range(1, Integer.MAX_VALUE)
        .concatMap(page -> getEventsFromPageReactive(dateFrom, dateTo, location, page))
        .takeWhile(eventsResponse -> !eventsResponse.getResults().isEmpty())
        .flatMapIterable(EventsResponse::getResults)
        .collectList();
  }

  public CompletableFuture<List<EventResponse>> getEventsFuture(
      LocalDate dateFrom, LocalDate dateTo, String location
  ) {
    return CompletableFuture.supplyAsync(() -> {
      List<EventResponse> allEventResponses = new ArrayList<>();
      int page = 1;
      while (true) {
        EventsResponse eventsResponse = getEventsFromPageFuture(dateFrom, dateTo, location, page).join();
        if (eventsResponse.getResults().isEmpty()) {
          break;
        }
        allEventResponses.addAll(eventsResponse.getResults());
        page++;
      }
      return allEventResponses;
    });
  }

  private <T> List<T> fetchData(String uri, Class<T[]> responseType) {
    log.debug("Start request");
    var response = kudaGoWebClient.get()
        .uri(uri)
        .retrieve()
        .bodyToMono(responseType)
        .retryWhen(Retry.fixedDelay(properties.maxRetries(), properties.retryDelay()))
        .doOnError(error -> log.error("An error has occurred {}", error.getMessage()))
        .onErrorResume(error -> Mono.just(responseType.cast(
            Array.newInstance(responseType.getComponentType(), 0))))
        .block();

    log.debug("Successfully fetched '{}' entries from '{}'", Objects.requireNonNull(response).length, uri);
    return Arrays.asList(response);
  }

  private Mono<EventsResponse> getEventsFromPageReactive(LocalDate dateFrom, LocalDate dateTo,
                                                         String location, int page) {
    return getEventsFromPage(dateFrom, dateTo, location, page)
        .onErrorResume(WebClientResponseException.NotFound.class,
            error -> Mono.just(new EventsResponse()))
        .onErrorResume(WebClientResponseException.BadRequest.class,
            error -> Mono.just(new EventsResponse()))
        .onErrorResume(WebClientResponseException.ServiceUnavailable.class,
            error -> Mono.error(new ServiceUnavailableException()))
        .onErrorResume(WebClientRequestException.class,
            error -> Mono.error(new ServiceUnavailableException()));
  }

  private CompletableFuture<EventsResponse> getEventsFromPageFuture(LocalDate dateFrom, LocalDate dateTo,
                                                                    String location, int page) {
    return getEventsFromPage(dateFrom, dateTo, location, page)
        .toFuture()
        .exceptionally(throwable -> {
          if (throwable instanceof WebClientResponseException) {
            return new EventsResponse();
          }
          log.error("Error occurred while data fetch from API");
          throw new ServiceUnavailableException();
        });
  }

  private Mono<EventsResponse> getEventsFromPage(LocalDate dateFrom, LocalDate dateTo, String location, int page) {
    return kudaGoWebClient.get()
        .uri(uriBuilder -> uriBuilder
            .path("/events/")
            .queryParam("actual_since", dateFrom.toString())
            .queryParam("actual_until", dateTo.toString())
            .queryParam("page", page)
            .queryParam("page_size", properties.pageSize())
            .queryParam("order_by", "is_free,price")
            .queryParam("text_format", "text")
            .queryParam("location", location)
            .queryParam("fields", "id,title,price,is_free,dates,place")
            .queryParam("expand", "place")
            .build())
        .retrieve()
        .bodyToMono(EventsResponse.class);
  }
}
