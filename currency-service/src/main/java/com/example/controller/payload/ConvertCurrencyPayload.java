package com.example.controller.payload;

import lombok.Builder;

@Builder
public record ConvertCurrencyPayload(
        String fromCurrency,
        String toCurrency,
        float amount
) {
}
