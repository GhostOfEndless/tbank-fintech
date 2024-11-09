package com.example.controller.dto;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
