package com.example.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Getter;

@Getter
public class DateResponse {

  @JsonProperty("start")
  private Instant start;
}
