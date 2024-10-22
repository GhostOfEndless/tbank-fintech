package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class PlaceResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("title")
    private String title;
}
