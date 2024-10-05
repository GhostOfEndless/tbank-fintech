package com.example.controller.dto;

import lombok.Builder;

@Builder
public record ConvertCurrencyDTO(
        String fromCurrency,
        String toCurrency,
        float convertedAmount
) {
}
