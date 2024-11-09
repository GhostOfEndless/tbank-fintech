package com.example.client.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class EventsResponse {

  private final List<EventResponse> results = new ArrayList<>();
  private int count;
  private String next;
  private String previous;
}
