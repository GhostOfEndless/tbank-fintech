package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.entity.Location;
import com.example.repository.jpa.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching locations from API...");
        kudaGoApiClient.fetchLocations()
                .forEach(payload -> {
                    if (!locationRepository.existsBySlug(payload.slug()))
                        locationRepository.save(
                                Location.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());
                });

        log.info("Locations added!");
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Location getLocationById(Long id) {
        return locationRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("location.not_found"));
    }

    public Location createLocation(String slug, String name) {
        return locationRepository.save(
                Location.builder()
                        .slug(slug)
                        .name(name)
                        .build());
    }

    public Location updateLocation(Long id, String slug, String name) {
        if (locationRepository.existsById(id)) {
            return locationRepository.save(
                    Location.builder()
                            .id(id)
                            .slug(slug)
                            .name(name)
                            .build());
        }
        throw new NoSuchElementException("location.not_found");
    }

    public void deleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return;
        }
        throw new NoSuchElementException("location.not_found");
    }
}
