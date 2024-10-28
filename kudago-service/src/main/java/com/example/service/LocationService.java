package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.controller.dto.LocationDTO;
import com.example.entity.CrudAction;
import com.example.entity.Location;
import com.example.entity.history.LocationMemento;
import com.example.exception.entity.LocationNotFoundException;
import com.example.repository.LocationRepository;
import com.example.service.history.LocationHistoryCaretaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationHistoryCaretaker historyCaretaker;
    private final LocationRepository locationRepository;
    private final KudaGoApiClient kudaGoApiClient;

    public void init() {
        log.info("Fetching locations from API...");
        kudaGoApiClient.fetchLocations()
                .forEach(payload -> {
                    if (!locationRepository.existsBySlug(payload.slug())) {
                        var location = locationRepository.save(
                                Location.builder()
                                        .slug(payload.slug())
                                        .name(payload.name())
                                        .build());
                        historyCaretaker.saveSnapshot(LocationMemento.createSnapshot(location, CrudAction.CREATE));
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
        var location = locationRepository.save(
                Location.builder()
                        .slug(slug)
                        .name(name)
                        .build());

        historyCaretaker.saveSnapshot(LocationMemento.createSnapshot(location, CrudAction.CREATE));

        return new LocationDTO(location.getId(), location.getSlug(), location.getName());
    }

    public LocationDTO updateLocation(Long id, String slug, String name) {
        var oldLocation = locationRepository.findById(id)
                .orElseThrow(() -> new LocationNotFoundException(id));

        oldLocation.setSlug(slug);
        oldLocation.setName(name);

        var location = locationRepository.save(oldLocation);
        historyCaretaker.saveSnapshot(LocationMemento.createSnapshot(location, CrudAction.UPDATE));

        return new LocationDTO(location.getId(), location.getSlug(), location.getName());
    }

    public void deleteLocation(Long id) {
        var location = locationRepository.findById(id).orElseThrow(() ->
                new LocationNotFoundException(id));
        historyCaretaker.saveSnapshot(LocationMemento.createSnapshot(location, CrudAction.DELETE));
        locationRepository.deleteById(id);
    }
}
