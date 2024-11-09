package com.example.controller;

import com.example.BaseIT;
import com.example.controller.dto.EventDTO;
import com.example.controller.dto.LocationDTO;
import com.example.controller.payload.EventPayload;
import com.example.controller.payload.LocationPayload;
import com.example.controller.payload.PlacePayload;
import com.fasterxml.jackson.core.type.TypeReference;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class LocationEventsRestControllerIT extends BaseIT {

  private static final String events_url = "/api/v2/events";
  private static final String location_url = "/api/v1/locations";

  private static final Supplier<Stream<Arguments>> invalidLocationPayload = () -> Stream.of(
      Arguments.of("", "", "", ""),
      Arguments.of("name", "1", Instant.now().truncatedTo(ChronoUnit.MINUTES).toString(), ""),
      Arguments.of("name", "1", "", Instant.now().truncatedTo(ChronoUnit.MINUTES).toString())
  );

  private static EventPayload eventPayload;

  @BeforeAll
  static void init() {
    eventPayload = EventPayload.builder()
        .name("name")
        .price("price")
        .free(false)
        .startDate(Instant.now().truncatedTo(ChronoUnit.MINUTES))
        .build();
  }

  private static Stream<Arguments> provideSearchParams() {
    return invalidLocationPayload.get();
  }

  @SneakyThrows
  @Test
  public void getEvent_success() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    var mvcResponse = mockMvc.perform(get(events_url + "/{id}", createdEvent.getId())
            .header("Authorization", userBearerToken))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var event = objectMapper.readValue(mvcResponse.getContentAsString(), EventDTO.class);

    assertAll(
        () -> assertThat(event.getName()).isEqualTo(createdEvent.getName()),
        () -> assertThat(event.getPrice()).isEqualTo(createdEvent.getPrice()),
        () -> assertThat(event.getStartDate()).isEqualTo(createdEvent.getStartDate())
    );

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @Test
  public void searchEvent_success() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());
    createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate().plus(Duration.ofDays(5)), createdLocation.id());

    var mvcResponse = mockMvc.perform(get(events_url + "/search")
            .header("Authorization", userBearerToken)
            .param("locationId", String.valueOf(createdLocation.id()))
            .param("name", eventPayload.name().substring(eventPayload.name().length() / 2))
            .param("dateFrom", createdEvent.getStartDate().minus(Duration.ofDays(1)).toString())
            .param("dateTo", createdEvent.getStartDate().plus(Duration.ofDays(1)).toString()))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var events = objectMapper.readValue(mvcResponse.getContentAsString(),
        new TypeReference<List<EventDTO>>() {
        });

    assertThat(events).isNotEmpty();
    assertThat(events).hasSize(1);
    assertAll(
        () -> assertThat(events.getFirst().getName()).isEqualTo(createdEvent.getName()),
        () -> assertThat(events.getFirst().getPrice()).isEqualTo(createdEvent.getPrice()),
        () -> assertThat(events.getFirst().getStartDate()).isEqualTo(createdEvent.getStartDate())
    );

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @ParameterizedTest
  @MethodSource("provideSearchParams")
  public void searchEvent_withNullParams(String name, String locationId, String dateFrom, String dateTo) {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    var mvcResponse = mockMvc.perform(get(events_url + "/search")
            .header("Authorization", userBearerToken)
            .param("locationId", String.valueOf(locationId.isEmpty() ?
                locationId : createdLocation.id()))
            .param("name", name)
            .param("dateFrom", dateFrom)
            .param("dateTo", dateTo))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var events = objectMapper.readValue(mvcResponse.getContentAsString(),
        new TypeReference<List<EventDTO>>() {
        });

    assertThat(events).isNotEmpty();
    assertThat(events).hasSize(1);
    assertAll(
        () -> assertThat(events.getFirst().getName()).isEqualTo(createdEvent.getName()),
        () -> assertThat(events.getFirst().getPrice()).isEqualTo(createdEvent.getPrice()),
        () -> assertThat(events.getFirst().getStartDate()).isEqualTo(createdEvent.getStartDate())
    );

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @Test
  public void getEventsByLocation_success() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    var mvcResponse = mockMvc.perform(get(events_url)
            .header("Authorization", userBearerToken)
            .param("locationId", String.valueOf(createdLocation.id())))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var events = objectMapper.readValue(mvcResponse.getContentAsString(),
        new TypeReference<List<EventDTO>>() {
        });

    assertThat(events).isNotEmpty();
    assertThat(events).hasSize(1);
    assertAll(
        () -> assertThat(events.getFirst().getName()).isEqualTo(createdEvent.getName()),
        () -> assertThat(events.getFirst().getPrice()).isEqualTo(createdEvent.getPrice()),
        () -> assertThat(events.getFirst().getStartDate()).isEqualTo(createdEvent.getStartDate())
    );

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @Test
  public void createEvent_relatedLocationNotFound() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    var updatePayload = EventPayload.builder()
        .name(createdEvent.getName() + "-updated")
        .price("new price")
        .free(true)
        .startDate(createdEvent.getStartDate())
        .placePayload(new PlacePayload(createdLocation.id() + 1))
        .build();

    mockMvc.perform(patch(events_url + "/{id}", createdEvent.getId())
            .header("Authorization", userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatePayload)))
        .andExpectAll(
            status().isBadRequest(),
            content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andReturn()
        .getResponse();

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @Test
  public void deleteEvent_success() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    deleteEvent(createdEvent.getId());
    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  @Test
  public void updateEvent_success() {
    var createdLocation = createLocation("test", "Test Location");
    var createdEvent = createEvent(eventPayload.name(), eventPayload.price(), eventPayload.free(),
        eventPayload.startDate(), createdLocation.id());

    var updatePayload = EventPayload.builder()
        .name(createdEvent.getName() + "-updated")
        .price("new price")
        .free(true)
        .startDate(createdEvent.getStartDate())
        .placePayload(new PlacePayload(createdLocation.id()))
        .build();

    var mvcResponse = mockMvc.perform(patch(events_url + "/{id}", createdEvent.getId())
            .header("Authorization", userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updatePayload)))
        .andExpectAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    var event = objectMapper.readValue(mvcResponse.getContentAsString(), EventDTO.class);

    assertAll(
        () -> assertThat(event.getName()).isEqualTo(updatePayload.name()),
        () -> assertThat(event.getPrice()).isEqualTo(updatePayload.price()),
        () -> assertThat(event.getStartDate()).isEqualTo(updatePayload.startDate()),
        () -> assertThat(event.isFree()).isEqualTo(updatePayload.free())
    );

    deleteLocation(createdLocation.id());
  }

  @SneakyThrows
  private LocationDTO createLocation(String slug, String name) {
    var payload = LocationPayload.builder()
        .slug(slug)
        .name(name)
        .build();

    var mvcResponse = mockMvc.perform(post(location_url)
            .header("Authorization", userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpectAll(
            status().isCreated(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    return objectMapper.readValue(mvcResponse.getContentAsString(), LocationDTO.class);
  }

  @SneakyThrows
  private EventDTO createEvent(String name, String price, boolean free, Instant startDate, Long placeId) {
    var payload = EventPayload.builder()
        .name(name)
        .price(price)
        .free(free)
        .startDate(startDate)
        .placePayload(new PlacePayload(placeId))
        .build();

    var mvcResponse = mockMvc.perform(post(events_url)
            .header("Authorization", userBearerToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(payload)))
        .andExpectAll(
            status().isCreated(),
            content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse();

    return objectMapper.readValue(mvcResponse.getContentAsString(), EventDTO.class);
  }

  @SneakyThrows
  private void deleteLocation(Long id) {
    mockMvc.perform(delete(location_url + "/{id}", id)
            .header("Authorization", userBearerToken))
        .andExpect(status().isNoContent());
  }

  @SneakyThrows
  private void deleteEvent(Long id) {
    mockMvc.perform(delete(events_url + "/{id}", id)
            .header("Authorization", userBearerToken))
        .andExpect(status().isNoContent());
  }
}
