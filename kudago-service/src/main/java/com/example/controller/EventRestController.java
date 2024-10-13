package com.example.controller;

import com.example.entity.Event;
import com.example.service.EventService;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService;

    @GetMapping
    public List<Event> getEvents(@Positive(message = "{budget.should_be_positive}")
                                 @RequestParam Float budget,
                                 @Pattern(message = "{currency_code.invalid_format}", regexp = "^[A-Za-z]{3}$")
                                 @RequestParam String currency,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 @RequestParam(required = false) LocalDate dateFrom,
                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                 @RequestParam(required = false) LocalDate dateTo) {
        return eventService.getEvents(budget, currency, dateFrom, dateTo);
    }
}
