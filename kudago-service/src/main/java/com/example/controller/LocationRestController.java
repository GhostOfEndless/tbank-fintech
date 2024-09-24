package com.example.controller;

import com.example.aspect.LogExecutionTime;
import com.example.controller.payload.LocationPayload;
import com.example.entity.Location;
import com.example.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@LogExecutionTime
@RestController
@RequestMapping("/api/v1/locations")
@RequiredArgsConstructor
public class LocationRestController {

    private final LocationService locationService;
    private final MessageSource messageSource;

    @GetMapping
    public List<Location> getAllLocations() {
        return locationService.getAllLocations();
    }

    @GetMapping("/{id}")
    public Location getLocationById(@PathVariable Long id) {
        return locationService.getLocationById(id);
    }

    @PostMapping
    public ResponseEntity<Location> createLocation(@Valid @RequestBody LocationPayload location) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(locationService.createLocation(location.slug(), location.name()));
    }

    @PutMapping("/{id}")
    public Location updateLocation(@PathVariable Long id,
                                   @Valid @RequestBody LocationPayload location) {
        return locationService.updateLocation(id, location.slug(), location.name());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ProblemDetail> handleNoSuchElementException(NoSuchElementException exception,
                                                                      Locale locale) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                        messageSource.getMessage(exception.getMessage(), new Object[0],
                                exception.getMessage(), locale)));
    }
}
