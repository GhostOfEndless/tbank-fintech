package com.example.service.history;

import com.example.entity.history.LocationHistory;
import com.example.entity.history.LocationMemento;
import com.example.repository.LocationHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationHistoryCaretaker {

  private final LocationHistoryRepository historyRepository;

  public void saveSnapshot(LocationMemento memento) {
    var history = LocationHistory.builder()
        .locationId(memento.id())
        .slug(memento.slug())
        .name(memento.name())
        .timestamp(memento.timestamp())
        .action(memento.action())
        .build();

    historyRepository.save(history);
  }

  public List<LocationMemento> getHistory(Long locationId) {
    return historyRepository.findByLocationIdOrderByTimestampDesc(locationId)
        .stream()
        .map(history -> new LocationMemento(
            history.getLocationId(),
            history.getSlug(),
            history.getName(),
            history.getTimestamp(),
            history.getAction()
        ))
        .toList();
  }
}
