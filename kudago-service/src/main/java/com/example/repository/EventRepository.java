package com.example.repository;

import com.example.entity.Event;
import com.example.entity.Location;
import com.example.repository.specification.EventSpecifications;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

  default List<Event> findEventsByParameters(String name, Location location, Instant fromDate, Instant toDate) {
    return findAll(EventSpecifications.withDynamicQuery(name, location, fromDate, toDate));
  }
}
