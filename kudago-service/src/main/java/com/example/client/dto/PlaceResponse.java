package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaceResponse {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("slug")
    private String slug;

    @JsonProperty("title")
    private String title;
}
