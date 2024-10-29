package com.example.repository;

import com.example.entity.history.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {

    List<LocationHistory> findByLocationIdOrderByTimestampDesc(Long locationId);
}
