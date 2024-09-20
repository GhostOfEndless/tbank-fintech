package com.example.controller.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryPayload(
        @JsonProperty("slug")
        @NotBlank(message = "{category.request.slug.is_blank}")
        @Size(min = 3, max = 25, message = "{category.request.slug.invalid_size}")
        String slug,
        @JsonProperty("name")
        @NotBlank(message = "{category.request.name.is_blank}")
        @Size(max = 50, message = "{category.request.name.invalid_size}")
        String name
) {
}
