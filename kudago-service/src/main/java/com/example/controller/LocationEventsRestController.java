package com.example.controller;

import com.example.controller.dto.EventDTO;
import com.example.controller.payload.EventPayload;
import com.example.service.EventService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/events")
@RequiredArgsConstructor
public class LocationEventsRestController {

  private final EventService eventService;

  @GetMapping("/{id}")
  public EventDTO getEventById(@PathVariable Long id) {
    return eventService.getById(id);
  }

  @GetMapping
  public List<EventDTO> getEventsByLocationId(@RequestParam Long locationId) {
    return eventService.getLocationWithEvents(locationId);
  }

  @PostMapping
  public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody EventPayload payload) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(eventService.create(payload));
  }

  @PatchMapping("/{id}")
  public EventDTO updateEvent(@PathVariable Long id, @Valid @RequestBody EventPayload payload) {
    return eventService.update(id, payload);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
    eventService.delete(id);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public List<EventDTO> searchEventsWithQuery(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) Long locationId,
                                              @RequestParam(required = false) Instant dateFrom,
                                              @RequestParam(required = false) Instant dateTo) {
    return eventService.searchEventsWithQuery(name, locationId, dateFrom, dateTo);
  }
}
