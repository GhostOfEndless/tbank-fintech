package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public record City(
        @JsonProperty("slug")
        String slug,
        @JsonProperty("coords")
        Coordinates coordinates
) {
}
