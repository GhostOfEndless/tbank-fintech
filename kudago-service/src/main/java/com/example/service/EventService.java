package com.example.service;

import com.example.client.CurrencyServiceApiClient;
import com.example.client.KudaGoApiClient;
import com.example.client.dto.EventResponse;
import com.example.controller.dto.EventDTO;
import com.example.controller.dto.LocationDTO;
import com.example.controller.payload.EventPayload;
import com.example.entity.Event;
import com.example.entity.Location;
import com.example.exception.DateBoundsException;
import com.example.exception.entity.EventNotfoundException;
import com.example.exception.entity.LocationNotFoundException;
import com.example.exception.entity.RelatedLocationNotFoundException;
import com.example.repository.EventRepository;
import com.example.repository.LocationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final KudaGoApiClient kudaGoApiClient;
    private final CurrencyServiceApiClient currencyApiClient;
    private final LocationRepository locationRepository;
    private final EventRepository eventRepository;

    public void init() {
        var dateBound = buildDateBounds(null, null);
        var locations = locationRepository.findAll();

        if (eventRepository.count() > 0) {
            return;
        }

        locations.forEach(location ->
                kudaGoApiClient.getEventsFuture(dateBound.from(), dateBound.to(), location.getSlug())
                        .join().forEach(eventResponse -> {

                            var event = Event.builder()
                                    .name(eventResponse.getTitle())
                                    .location(location)
                                    .startDate(eventResponse.getDates().getLast().getStart())
                                    .price(eventResponse.getPrice())
                                    .free(eventResponse.isFree())
                                    .build();

                            eventRepository.save(event);
                        })
        );
    }

    public Mono<List<EventResponse>> fetchEventsReactive(Float budget, String currencyCode,
                                                         LocalDate dateFrom, LocalDate dateTo, String location) {
        var dateBounds = buildDateBounds(dateFrom, dateTo);

        return Mono.zip(
                currencyApiClient.convertBudgetToRublesReactive(budget, currencyCode),
                kudaGoApiClient.getEventsReactive(dateBounds.from(), dateBounds.to(), location)
        ).map(tuple -> {
            Float convertedBudget = tuple.getT1();
            List<EventResponse> eventResponses = tuple.getT2();

            return eventResponses.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .toList();
        });
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<EventResponse>> fetchEventsFuture(Float budget, String currencyCode,
                                                                    LocalDate dateFrom, LocalDate dateTo,
                                                                    String location) {
        var dateBounds = buildDateBounds(dateFrom, dateTo);
        var convertedBudgetFuture = currencyApiClient.convertBudgetToRublesFuture(budget, currencyCode);
        var eventsFuture = kudaGoApiClient.getEventsFuture(dateBounds.from(), dateBounds.to(), location);
        var resultFuture = new CompletableFuture<List<EventResponse>>();

        convertedBudgetFuture.thenAcceptBoth(eventsFuture, (convertedBudget, events) -> {
            List<EventResponse> filteredEventResponses = events.stream()
                    .filter(event -> event.isFitsBudget(convertedBudget))
                    .collect(Collectors.toList());
            resultFuture.complete(filteredEventResponses);
        }).exceptionally(ex -> {
            resultFuture.completeExceptionally(ex);
            return null;
        });

        return resultFuture;
    }

    @Transactional
    public EventDTO getById(Long id) {
        return eventToDTO(eventRepository.findById(id)
                .orElseThrow(() -> new EventNotfoundException(id)));
    }

    public List<EventDTO> getLocationWithEvents(Long locationId) {
        var location = locationRepository.findByIdWithEvents(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));

        return location.getEvents()
                .stream()
                .map(this::eventToDTO)
                .toList();
    }

    public List<EventDTO> searchEventsWithQuery(String name, Long locationId, Instant dateFrom, Instant dateTo) {
        Location location = null;

        if (locationId != null) {
            location = locationRepository.findById(locationId)
                    .orElseThrow(() -> new RelatedLocationNotFoundException(locationId));
        }

        return eventRepository.findEventsByParameters(name, location, dateFrom, dateTo)
                .stream()
                .map(this::eventToDTO)
                .toList();
    }

    public EventDTO create(EventPayload payload) {
        var location = getLocationById(payload.placePayload().id());

        var event = Event.builder()
                .name(payload.name())
                .price(payload.price())
                .free(payload.free())
                .startDate(payload.startDate())
                .location(location)
                .build();

        return eventToDTO(eventRepository.save(event));
    }

    @Transactional
    public EventDTO update(Long id, EventPayload payload) {
        var location = getLocationById(payload.placePayload().id());
        var oldEvent = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotfoundException(id));

        var updatedEvent = Event.builder()
                .id(oldEvent.getId())
                .name(payload.name())
                .price(payload.price())
                .free(payload.free())
                .startDate(payload.startDate())
                .location(location)
                .build();

        return eventToDTO(eventRepository.save(updatedEvent));
    }

    public void delete(Long id) {
        var event = eventRepository.findById(id)
                .orElseThrow(() -> new EventNotfoundException(id));

        eventRepository.delete(event);
    }

    private Location getLocationById(Long id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new RelatedLocationNotFoundException(id));
    }

    private EventDTO eventToDTO(Event event) {
        return EventDTO.builder()
                .id(event.getId())
                .place(locationToDTO(event.getLocation()))
                .name(event.getName())
                .startDate(event.getStartDate())
                .price(event.getPrice())
                .free(event.isFree())
                .build();
    }

    private LocationDTO locationToDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .slug(location.getSlug())
                .name(location.getName())
                .build();
    }

    private DateBounds buildDateBounds(LocalDate dateFrom, LocalDate dateTo) {
        if (Objects.isNull(dateFrom) && Objects.isNull(dateTo)) {
            var currentDate = LocalDate.now();
            dateFrom = currentDate.with(DayOfWeek.MONDAY);
            dateTo = currentDate.with(DayOfWeek.SUNDAY);
        } else if (Objects.isNull(dateFrom) || Objects.isNull(dateTo) || dateFrom.isAfter(dateTo)) {
            throw new DateBoundsException();
        }

        return new DateBounds(dateFrom, dateTo);
    }

    record DateBounds(
            LocalDate from,
            LocalDate to
    ) {
    }
}
