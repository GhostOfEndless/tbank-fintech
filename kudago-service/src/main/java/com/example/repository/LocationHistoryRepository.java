package com.example.repository;

import com.example.entity.history.LocationHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {

  List<LocationHistory> findByLocationIdOrderByTimestampDesc(Long locationId);
}
