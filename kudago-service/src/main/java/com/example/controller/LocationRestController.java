package com.example.controller;

import com.example.aspect.LogExecutionTime;
import com.example.controller.dto.LocationDTO;
import com.example.controller.payload.LocationPayload;
import com.example.entity.history.LocationMemento;
import com.example.service.history.LocationHistoryCaretaker;
import com.example.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationRestController {

    private final LocationHistoryCaretaker historyCaretaker;
    private final LocationService locationService;

    @GetMapping
    public List<LocationDTO> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public LocationDTO getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }

    @GetMapping("/{id}/history")
    public List<LocationMemento> getHistory(@PathVariable Long id) {
        return historyCaretaker.getHistory(id);
    }

    @PostMapping
    public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationPayload location) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationService.createLocation(location.slug(), location.name()));
    }

    @PutMapping("/{id}")
    public LocationDTO updateLocation(@PathVariable Long id,
                                      @Valid @RequestBody LocationPayload location) {
        return locationService.updateLocation(id, location.slug(), location.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
