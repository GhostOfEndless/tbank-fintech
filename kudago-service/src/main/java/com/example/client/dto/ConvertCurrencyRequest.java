package com.example.client.dto;

import lombok.Builder;

@Builder
public record ConvertCurrencyRequest(
    String fromCurrency,
    String toCurrency,
    Float amount
) {
}
