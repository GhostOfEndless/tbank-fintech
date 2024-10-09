package com.example.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Ответ с курсом валюты")
public record CurrencyRateDTO(
        @Schema(description = "Код валюты", example = "USD")
        String currency,
        @Schema(description = "Курс валюты", example = "1.0")
        float rate
) {
}
