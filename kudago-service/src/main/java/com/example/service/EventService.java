package com.example.service;


import com.example.client.CurrencyServiceApiClient;
import com.example.client.KudaGoApiClient;
import com.example.entity.Event;
import com.example.exception.DateBoundsException;
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

    public Mono<List<Event>> getEventsReactive(@NotNull Float budget, @NotNull String currencyCode,
                                               LocalDate dateFrom, LocalDate dateTo) {
        var dateBounds = getDateBounds(dateFrom, dateTo);

        return Mono.zip(
                currencyApiClient.convertBudgetToRublesReactive(budget, currencyCode),
                kudaGoApiClient.getEventsReactive(dateBounds.from(), dateBounds.to())
        ).map(tuple -> {
            Float convertedBudget = tuple.getT1();
            List<Event> events = tuple.getT2();

            return events.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .toList();
        });
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Event>> getEventsFuture(@NotNull Float budget, @NotNull String currencyCode,
                                                          LocalDate dateFrom, LocalDate dateTo) {
        var dateBounds = getDateBounds(dateFrom, dateTo);
        var convertedBudgetFuture = currencyApiClient.convertBudgetToRublesFuture(budget, currencyCode);
        var eventsFuture = kudaGoApiClient.getEventsFuture(dateBounds.from(), dateBounds.to());
        var resultFuture = new CompletableFuture<List<Event>>();

        convertedBudgetFuture.thenAcceptBoth(eventsFuture, (convertedBudget, events) -> {
            List<Event> filteredEvents = events.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .collect(Collectors.toList());
            resultFuture.complete(filteredEvents);
        }).exceptionally(ex -> {
            resultFuture.completeExceptionally(ex);
            return null;
        });

        return resultFuture;
    }

    private DateBounds getDateBounds(LocalDate dateFrom, LocalDate dateTo) {
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
