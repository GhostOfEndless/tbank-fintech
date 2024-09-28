package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Location;
import com.example.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository repository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching locations from API...");
        kudaGoApiClient.fetchLocations()
                .forEach(payload -> repository.save(
                        Location.builder()
                                .slug(payload.slug())
                                .name(payload.name())
                                .build()));
        log.info("Locations added!");
    }

    public List<Location> getAllLocations() {
        return repository.findAll();
    }

    public Location getLocationById(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new NoSuchElementException("location.not_found"));
    }

    public Location createLocation(String slug, String name) {
        return repository.save(
                Location.builder()
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public Location updateLocation(Long id, String slug, String name) {
        if (repository.existsById(id)) {
            return repository.save(
                    Location.builder()
                            .id(id)
                            .slug(slug)
                            .name(name)
                            .build());
        }
        throw new NoSuchElementException("location.not_found");
    }

    public void deleteLocation(Long id) {
        if (repository.existsById(id)) {
            repository.delete(id);
            return;
        }
        throw new NoSuchElementException("location.not_found");
    }
}
