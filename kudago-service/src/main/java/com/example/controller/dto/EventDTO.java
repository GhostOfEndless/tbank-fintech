package com.example.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
@AllArgsConstructor
public class EventDTO {

    private Long id;
    private String name;
    private Instant startDate;
    private String price;
    private boolean free;
    private LocationDTO place;
}
