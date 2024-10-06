package com.example.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ConvertCurrencyPayload(
        @NotBlank(message = "{currency.from.is_blank}")
        @Size(min = 3, max = 3, message = "{currency.from.invalid_size}")
        String fromCurrency,
        @NotBlank(message = "{currency.to.is_blank}")
        @Size(min = 3, max = 3, message = "{currency.to.invalid_size}")
        String toCurrency,
        @Positive(message = "{currency.amount.should_be_positive}")
        @NotNull(message = "{currency.amount.is_null}")
        Float amount
) {
}
