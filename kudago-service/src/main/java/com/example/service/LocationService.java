package com.example.service;

import com.example.entity.Location;
import com.example.repository.LocationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository repository;

    @PostConstruct
    private void init() {
        log.info("LocationService initialization...");
    }

    public List<Location> getAllLocations() {
        return repository.findAll();
    }

    public Location getLocationById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Location not found"));
    }

    public Location createLocation(String slug, String name) {
        return repository.save(
                Location.builder()
                .slug(slug)
                .name(name)
                .build());
    }

    public Location updateLocation(Long id, String slug, String name) {
        return repository.save(
                Location.builder()
                        .id(id)
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public void deleteLocation(Long id) {
        repository.delete(id);
    }
}
