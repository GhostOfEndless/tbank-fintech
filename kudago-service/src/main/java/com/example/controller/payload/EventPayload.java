package com.example.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record EventPayload(
        @NotBlank(message = "{event.request.name.is_blank}")
        String name,

        @NotNull(message = "{event.start_date.is_null}")
        Instant startDate,

        @NotNull(message = "{event.price.is_null}")
        String price,

        boolean free,

        @NotNull(message = "{event.place.is_null}")
        @JsonProperty("place")
        PlacePayload placePayload
) {
}