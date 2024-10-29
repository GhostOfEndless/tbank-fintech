package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.controller.dto.LocationDTO;
import com.example.entity.CrudAction;
import com.example.entity.Location;
import com.example.exception.entity.LocationExistsException;
import com.example.exception.entity.LocationNotFoundException;
import com.example.repository.LocationRepository;
import com.example.service.observer.location.LocationPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationPublisher locationPublisher;
    private final LocationRepository locationRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching locations from API...");
        kudaGoApiClient.fetchLocations()
                .forEach(payload -> {
                    if (!locationRepository.existsBySlug(payload.slug())) {
                        var savedLocation = locationRepository.save(
                                Location.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());

                        locationPublisher.notifyObservers(savedLocation, CrudAction.CREATE);
                    }
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
        if (locationRepository.existsBySlug(slug)) {
            throw new LocationExistsException(slug);
        }

        var savedLocation = locationRepository.save(
                Location.builder()
                        .slug(slug)
                        .name(name)
                        .build());

        locationPublisher.notifyObservers(savedLocation, CrudAction.CREATE);

        return new LocationDTO(savedLocation.getId(), savedLocation.getSlug(), savedLocation.getName());
    }

    public LocationDTO updateLocation(Long id, String slug, String name) {
        var oldLocation = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        if (locationRepository.existsBySlug(slug)) {
            throw new LocationExistsException(slug);
        }

        oldLocation.setSlug(slug);
        oldLocation.setName(name);

        var updatedLocation = locationRepository.save(oldLocation);
        locationPublisher.notifyObservers(updatedLocation, CrudAction.UPDATE);

        return new LocationDTO(updatedLocation.getId(), updatedLocation.getSlug(), updatedLocation.getName());
    }

    public void deleteLocation(Long id) {
        var location = locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));
        locationPublisher.notifyObservers(location, CrudAction.DELETE);
        locationRepository.deleteById(id);
    }
}
