package com.example.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventsResponse {

    private int count;
    private String next;
    private String previous;
    private List<Event> results = new ArrayList<>();
}
