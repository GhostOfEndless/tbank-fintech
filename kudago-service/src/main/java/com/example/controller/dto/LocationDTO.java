package com.example.controller.dto;

import lombok.Builder;

@Builder
public record LocationDTO(
        Long id,
        String slug,
        String name
) {
}
