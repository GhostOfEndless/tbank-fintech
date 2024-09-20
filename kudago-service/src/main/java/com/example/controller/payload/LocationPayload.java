package com.example.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LocationPayload(
        @JsonProperty("slug")
        @NotBlank(message = "{location.request.slug.is_blank}")
        @Size(min = 3, max = 5, message = "{location.request.slug.invalid_size}")
        String slug,
        @JsonProperty("name")
        @NotBlank(message = "{location.request.name.is_blank}")
        @Size(max = 50, message = "{location.request.name.invalid_size}")
        String name
) {
}
