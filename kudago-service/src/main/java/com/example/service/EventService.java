package com.example.service;


import com.example.client.CurrencyServiceApiClient;
import com.example.client.KudaGoApiClient;
import com.example.entity.Event;
import com.example.exception.DateBoundsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final KudaGoApiClient kudaGoApiClient;
    private final CurrencyServiceApiClient currencyApiClient;

    public List<Event> getEvents(@NotNull Float budget, @NotNull String currencyCode,
                                 LocalDate dateFrom, LocalDate dateTo) {
        if (Objects.isNull(dateFrom) && Objects.isNull(dateTo)) {
            var currentDate = LocalDate.now();
            dateFrom = currentDate.with(DayOfWeek.MONDAY);
            dateTo = currentDate.with(DayOfWeek.SUNDAY);
        } else {
            validateDateBound(dateFrom, dateTo);
        }

        var convertedBudget = currencyApiClient.convertBudgetToRubles(budget, currencyCode);

        return kudaGoApiClient.getEventsParallel(dateFrom, dateTo, convertedBudget);
    }

    private void validateDateBound(LocalDate dateFrom, LocalDate dateTo) {
        if (Objects.isNull(dateFrom) && !Objects.isNull(dateTo) ||
                !Objects.isNull(dateFrom) && Objects.isNull(dateTo) || dateFrom.isAfter(dateTo)) {
            throw new DateBoundsException();
        }
    }
}
