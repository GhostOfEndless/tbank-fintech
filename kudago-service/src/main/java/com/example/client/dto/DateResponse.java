package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

import java.time.Instant;

@Getter
public class DateResponse {

    @JsonProperty("start")
    private Instant start;
}
