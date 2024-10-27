package com.example.repository;

import com.example.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.events WHERE l.id = :id")
    Optional<Location> findByIdWithEvents(@Param("id") Long id);

    boolean existsBySlug(String slug);
}
