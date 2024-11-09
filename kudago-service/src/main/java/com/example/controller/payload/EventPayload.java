package com.example.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Builder;

@Builder
public record EventPayload(
    @NotBlank(message = "{event.request.name.is_blank}")
    String name,

    @NotNull(message = "{event.start_date.is_null}")
    Instant startDate,

    @NotNull(message = "{event.price.is_null}")
    String price,

    boolean free,

    @Valid
    @NotNull(message = "{event.place.is_null}")
    @JsonProperty("place")
    PlacePayload placePayload
) {
}
