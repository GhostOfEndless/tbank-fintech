package com.example.controller.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
@Schema(description = "Запрос на конвертацию валюты")
public record ConvertCurrencyPayload(
        @NotBlank(message = "{currency.from.is_blank}")
        @Pattern(message = "{currency.from.invalid_format}", regexp = "^[A-Z]{3}$")
        @Pattern(message = "{currency.from.invalid_format}", regexp = "^[A-Z]{3}$")
        String fromCurrency,
        @NotBlank(message = "{currency.to.is_blank}")
        @Pattern(message = "{currency.to.invalid_format}", regexp = "^[A-Z]{3}$")
        @Schema(description = "Код целевой валюты", example = "EUR")
        String toCurrency,
        @Positive(message = "{currency.amount.should_be_positive}")
        @NotNull(message = "{currency.amount.is_null}")
        @Schema(description = "Сумма для конвертации", example = "100.0")
        Float amount
) {
}
