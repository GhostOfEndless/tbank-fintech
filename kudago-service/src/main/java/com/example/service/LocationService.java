package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.controller.dto.LocationDTO;
import com.example.entity.Location;
import com.example.exception.LocationNotFoundException;
import com.example.repository.jpa.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream().map(location ->
                new LocationDTO(location.getId(), location.getSlug(), location.getName())).toList();
    }

    public LocationDTO getLocationById(Long id) {
        var location = locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));

        return new LocationDTO(location.getId(), location.getSlug(), location.getName());
    }

    public LocationDTO createLocation(String slug, String name) {
        var location = locationRepository.save(
                Location.builder()
                        .slug(slug)
                        .name(name)
                        .build());

        return new LocationDTO(location.getId(), location.getSlug(), location.getName());
    }

    public LocationDTO updateLocation(Long id, String slug, String name) {
            var oldLocation = locationRepository.findById(id)
                    .orElseThrow(() -> new LocationNotFoundException(id));

            oldLocation.setSlug(slug);
            oldLocation.setName(name);

            var location = locationRepository.save(oldLocation);

            return new LocationDTO(location.getId(), location.getSlug(), location.getName());
    }

    public void deleteLocation(Long id) {
        if (locationRepository.existsById(id)) {
            locationRepository.deleteById(id);
            return;
        }
        throw new LocationNotFoundException(id);
    }
}
