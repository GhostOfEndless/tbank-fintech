package com.example.controller;

import com.example.client.dto.EventResponse;
import com.example.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "События", description = "API для работы с мероприятиями")
@Validated
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventRestController {

    private final EventService eventService;

    @Operation(
            summary = "Получение списка мероприятий (реактивный подход)",
            description = "Возвращает отфильтрованный список мероприятий с учетом бюджета, валюты и дат"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка мероприятий",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Сервис временно недоступен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/reactive")
    public List<EventResponse> getEventsReactive(
            @Parameter(description = "Бюджет пользователя (должен быть положительным)")
            @Positive(message = "{budget.should_be_positive}")
            @RequestParam Float budget,

            @Parameter(description = "Код валюты (3 буквы)", example = "RUB")
            @Pattern(message = "{currency_code.invalid_format}", regexp = "^[A-Za-z]{3}$")
            @RequestParam String currency,

            @Parameter(description = "Начальная дата (формат: YYYY-MM-DD)", example = "2024-10-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateFrom,

            @Parameter(description = "Конечная дата (формат: YYYY-MM-DD)", example = "2024-12-31")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateTo) {
        return eventService.fetchEventsReactive(budget, currency, dateFrom, dateTo, "kzn").block();
    }

    @Operation(
            summary = "Получение списка мероприятий (асинхронный подход)",
            description = "Возвращает отфильтрованный список мероприятий с учетом бюджета, валюты и дат"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение списка мероприятий",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = EventResponse.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Некорректные параметры запроса",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Сервис временно недоступен",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    )
            )
    })
    @GetMapping("/future")
    public List<EventResponse> getEventsFuture(
            @Parameter(description = "Бюджет пользователя (должен быть положительным)")
            @Positive(message = "{budget.should_be_positive}")
            @RequestParam Float budget,

            @Parameter(description = "Код валюты (3 буквы)", example = "RUB")
            @Pattern(message = "{currency_code.invalid_format}", regexp = "^[A-Za-z]{3}$")
            @RequestParam String currency,

            @Parameter(description = "Начальная дата (формат: YYYY-MM-DD)", example = "2024-10-01")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateFrom,

            @Parameter(description = "Конечная дата (формат: YYYY-MM-DD)", example = "2024-12-31")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            @RequestParam(required = false) LocalDate dateTo) {
        return eventService.fetchEventsFuture(budget, currency, dateFrom, dateTo, "kzn").join();
    }
}
