package com.example.service;

import com.example.client.KudaGoApiClient;
import com.example.controller.dto.LocationDTO;
import com.example.controller.payload.LocationPayload;
import com.example.entity.Location;
import com.example.exception.entity.LocationNotFoundException;
import com.example.repository.jpa.LocationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class LocationServiceTest {

    private final LocationDTO locationDTO = LocationDTO.builder()
            .id(1L)
            .name("test")
            .slug("test")
            .build();

    private final Location location = Location.builder()
            .id(locationDTO.id())
            .slug(locationDTO.slug())
            .name(locationDTO.name())
            .build();

    @Mock
    LocationRepository repository;
    @Mock
    KudaGoApiClient kudaGoApiClient;
    @InjectMocks
    LocationService service;

    @Test
    @Tag("Create")
    @DisplayName("Should successfully create a new location")
    public void createLocation_success() {
        var payloadLocation = Location.builder()
                .name(location.getName())
                .slug(location.getSlug())
                .build();

        var savedLocation = Location.builder()
                .id(location.getId())
                .name(location.getName())
                .slug(location.getSlug())
                .build();

        doReturn(savedLocation).when(repository).save(payloadLocation);

        var newLocation = service.createLocation(payloadLocation.getSlug(), payloadLocation.getName());

        assertAll(
                () -> assertThat(newLocation.name()).isEqualTo(payloadLocation.getName()),
                () -> assertThat(newLocation.slug()).isEqualTo(payloadLocation.getSlug())
        );
        verify(repository).save(payloadLocation);
        verifyNoMoreInteractions(repository);
    }

    @Test
    @Tag("Init")
    @DisplayName("Should fetch locations from API during initialization")
    public void init_invokesApi() {
        var payload = LocationPayload.builder()
                .name("test")
                .slug("test")
                .build();
        doReturn(List.of(payload)).when(kudaGoApiClient).fetchLocations();

        service.init();

        verify(kudaGoApiClient).fetchLocations();
        verifyNoMoreInteractions(kudaGoApiClient);
    }

    @Nested
    @Tag("GetAll")
    @DisplayName("Get all locations tests")
    class GetAllLocationsTests {

        @Test
        @DisplayName("Should return empty list when no locations exist")
        public void getAllLocations_empty() {
            doReturn(List.of()).when(repository).findAll();

            var locations = service.getAllLocations();

            assertThat(locations).isEmpty();
        }

        @Test
        @DisplayName("Should return list of locations when locations exist")
        public void getAllLocations_notEmpty() {
            doReturn(List.of(location)).when(repository).findAll();

            var locations = service.getAllLocations();

            assertThat(locations).contains(locationDTO);
        }
    }

    @Nested
    @Tag("GetById")
    @DisplayName("Get location by ID tests")
    class GetLocationByIdTest {

        @Test
        @DisplayName("Should return location when found by ID")
        public void getById_success() {
            doReturn(Optional.of(location)).when(repository).findById(location.getId());

            var receivedLocation = service.getLocationById(location.getId());

            assertThat(receivedLocation).isEqualTo(locationDTO);
        }

        @Test
        @DisplayName("Should throw exception when location not found by ID")
        public void getById_notFound() {
            assertThatExceptionOfType(LocationNotFoundException.class)
                    .isThrownBy(() -> service.getLocationById(location.getId()))
                    .withMessage("location.not_found");
        }
    }

    @Nested
    @Tag("Update")
    @DisplayName("Update location tests")
    class UpdateLocationTest {

        @Test
        @DisplayName("Should successfully update an existing location")
        public void updateLocation_success() {
            var changedLocation = Location.builder()
                    .name(location.getName() + "-updated")
                    .slug(location.getSlug())
                    .id(location.getId())
                    .build();

            var changedLocationDTO = LocationDTO.builder()
                    .name(location.getName() + "-updated")
                    .slug(location.getSlug())
                    .id(location.getId())
                    .build();

            doReturn(Optional.of(changedLocation)).when(repository).findById(changedLocation.getId());
            doReturn(changedLocation).when(repository).save(changedLocation);

            var updatedLocation = service.updateLocation(location.getId(),
                    changedLocation.getSlug(), changedLocation.getName());

            assertThat(changedLocationDTO).isEqualTo(updatedLocation);
            verify(repository).save(changedLocation);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent location")
        public void updateLocation_notFound() {
            var changedLocation = Location.builder()
                    .name(location.getName() + "-updated")
                    .slug(location.getSlug())
                    .id(location.getId())
                    .build();
            doReturn(Optional.empty()).when(repository).findById(changedLocation.getId());

            assertThatExceptionOfType(LocationNotFoundException.class)
                    .isThrownBy(() -> service.updateLocation(
                            location.getId(),
                            changedLocation.getSlug(),
                            changedLocation.getName()))
                    .withMessage("location.not_found");
        }
    }

    @Nested
    @Tag("Delete")
    @DisplayName("Delete location tests")
    class DeleteLocationTest {

        @Test
        @DisplayName("Should successfully delete an existing location")
        public void deleteLocation_success() {
            doReturn(true).when(repository).existsById(location.getId());

            service.deleteLocation(location.getId());

            verify(repository).deleteById(location.getId());
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent location")
        public void deleteLocation_notFound() {
            doReturn(false).when(repository).existsById(location.getId());

            assertThatExceptionOfType(LocationNotFoundException.class)
                    .isThrownBy(() -> service.deleteLocation(location.getId()))
                    .withMessage("location.not_found");
        }
    }
}
