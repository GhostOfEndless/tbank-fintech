package com.example.controller;

import com.example.controller.dto.ConvertCurrencyDTO;
import com.example.controller.dto.CurrencyRateDTO;
import com.example.controller.payload.ConvertCurrencyPayload;
import com.example.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/currencies")
@Validated
@Tag(name = "Валюты", description = "API для работы с валютами")
public class CurrencyRestController {

    private final CurrencyService currencyService;

    @Operation(summary = "Получить курс валюты", description = "Получить курс для указанной валюты")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = CurrencyRateDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный код валюты",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/rate/{code}")
    public CurrencyRateDTO getCurrencyRate(@PathVariable("code")
                                           @NotBlank(message = "currency.code.is_blank")
                                           @Pattern(message = "currency.code.invalid_format", regexp = "^[A-Z]{3}$")
                                           String code) {
        var rate = currencyService.getCurrencyRate(code);
        return CurrencyRateDTO.builder()
                .currency(code)
                .rate(rate)
                .build();
    }

    @Operation(summary = "Конвертировать валюту", description = "Конвертировать сумму из одной валюты в другую")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция",
                    content = @Content(schema = @Schema(implementation = ConvertCurrencyDTO.class))),
            @ApiResponse(responseCode = "400", description = "Неверный запрос",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Валюта не найдена",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "503", description = "Сервис недоступен",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)),
                    headers = @Header(name = "Retry-After", schema = @Schema(type = "integer")))
    })
    @PostMapping("/convert")
    public ConvertCurrencyDTO convertCurrency(@Valid @RequestBody ConvertCurrencyPayload payload) {
        var convertedAmount = currencyService.convertToCurrency(payload.fromCurrency(),
                payload.toCurrency(), payload.amount());

        return ConvertCurrencyDTO.builder()
                .fromCurrency(payload.fromCurrency())
                .toCurrency(payload.toCurrency())
                .convertedAmount(convertedAmount)
                .build();
    }
}
