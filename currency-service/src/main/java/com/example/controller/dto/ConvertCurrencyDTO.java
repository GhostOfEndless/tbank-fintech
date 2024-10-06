package com.example.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Ответ с результатом конвертации валюты")
public record ConvertCurrencyDTO(
        @Schema(description = "Код исходной валюты", example = "USD")
        String fromCurrency,
        @Schema(description = "Код целевой валюты", example = "EUR")
        String toCurrency,
        @Schema(description = "Конвертированная сумма", example = "85.5")
        float convertedAmount
) {
}
