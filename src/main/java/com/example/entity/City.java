package com.example.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @JsonProperty("slug")
    private String slug;
    @JsonProperty("coords")
    private Coordinates coordinates;

    @ToString
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Coordinates {

        @JsonProperty("lat")
        private float latitude;
        @JsonProperty("lon")
        private float longitude;
    }
}
