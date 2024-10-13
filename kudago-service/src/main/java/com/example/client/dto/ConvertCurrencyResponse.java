package com.example.client.dto;

import lombok.Builder;

@Builder
public record ConvertCurrencyResponse(
        String fromCurrency,
        String toCurrency,
        Float convertedAmount
) {
}
