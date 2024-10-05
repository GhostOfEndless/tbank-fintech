package com.example.controller.dto;

import lombok.Builder;

@Builder
public record CurrencyRateDTO(
        String currency,
        float rate
) {
}
