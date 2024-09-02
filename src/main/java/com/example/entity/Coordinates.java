package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Coordinates(
        @JsonProperty("lat")
        float latitude,
        @JsonProperty("lon")
        float longitude
) {
}
