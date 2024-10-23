package com.example.service;


import com.example.client.CurrencyServiceApiClient;
import com.example.client.KudaGoApiClient;
import com.example.client.dto.EventResponse;
import com.example.entity.Event;
import com.example.exception.DateBoundsException;
import com.example.repository.jpa.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final KudaGoApiClient kudaGoApiClient;
    private final CurrencyServiceApiClient currencyApiClient;
    private final LocationService locationService;
    private final EventRepository eventRepository;

    public void init() {
        var dateBound = buildDateBounds(null, null);
        var locations = locationService.getAllLocations();

        locations.forEach(location ->
                kudaGoApiClient.getEventsFuture(dateBound.from(), dateBound.to(), location.getSlug())
                        .join().forEach(eventResponse -> {

                            var event = Event.builder()
                                    .id(eventResponse.getId())
                                    .name(eventResponse.getTitle())
                                    .location(location)
                                    .startDate(eventResponse.getDates().getFirst().getStart())
                                    .price(eventResponse.getPrice())
                                    .free(eventResponse.isFree())
                                    .build();

                            if (eventRepository.existsById(event.getId())) {
                                eventRepository.save(event);
                            }
                        }));
    }

    public Mono<List<EventResponse>> fetchEventsReactive(@NotNull Float budget, @NotNull String currencyCode,
                                                         LocalDate dateFrom, LocalDate dateTo, String location) {
        var dateBounds = buildDateBounds(dateFrom, dateTo);

        return Mono.zip(
                currencyApiClient.convertBudgetToRublesReactive(budget, currencyCode),
                kudaGoApiClient.getEventsReactive(dateBounds.from(), dateBounds.to(), location)
        ).map(tuple -> {
            Float convertedBudget = tuple.getT1();
            List<EventResponse> eventResponses = tuple.getT2();

            return eventResponses.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .toList();
        });
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<EventResponse>> fetchEventsFuture(@NotNull Float budget, @NotNull String currencyCode,
                                                                    LocalDate dateFrom, LocalDate dateTo,
                                                                    String location) {
        var dateBounds = buildDateBounds(dateFrom, dateTo);
        var convertedBudgetFuture = currencyApiClient.convertBudgetToRublesFuture(budget, currencyCode);
        var eventsFuture = kudaGoApiClient.getEventsFuture(dateBounds.from(), dateBounds.to(), location);
        var resultFuture = new CompletableFuture<List<EventResponse>>();

        convertedBudgetFuture.thenAcceptBoth(eventsFuture, (convertedBudget, events) -> {
            List<EventResponse> filteredEventResponses = events.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .collect(Collectors.toList());
            resultFuture.complete(filteredEventResponses);
        }).exceptionally(ex -> {
            resultFuture.completeExceptionally(ex);
            return null;
        });

        return resultFuture;
    }

    private DateBounds buildDateBounds(LocalDate dateFrom, LocalDate dateTo) {
        if (Objects.isNull(dateFrom) && Objects.isNull(dateTo)) {
            var currentDate = LocalDate.now();
            dateFrom = currentDate.with(DayOfWeek.MONDAY);
            dateTo = currentDate.with(DayOfWeek.SUNDAY);
        } else if (Objects.isNull(dateFrom) || Objects.isNull(dateTo) || dateFrom.isAfter(dateTo)) {
            throw new DateBoundsException();
        }

        return new DateBounds(dateFrom, dateTo);
    }

    record DateBounds(
            LocalDate from,
            LocalDate to
    ) {
    }
}
