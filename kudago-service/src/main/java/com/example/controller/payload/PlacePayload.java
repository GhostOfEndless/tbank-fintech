package com.example.controller.payload;

import jakarta.validation.constraints.NotNull;

public record PlacePayload(
        @NotNull(message = "{event.place.id}")
        Long id
) {
}
