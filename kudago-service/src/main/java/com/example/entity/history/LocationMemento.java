package com.example.entity.history;

import com.example.entity.CrudAction;
import com.example.entity.Location;
import java.time.LocalDateTime;

public record LocationMemento(
    Long id,
    String slug,
    String name,
    LocalDateTime timestamp,
    CrudAction action
) {
  public static LocationMemento createSnapshot(Location location, CrudAction action) {
    return new LocationMemento(
        location.getId(),
        location.getSlug(),
        location.getName(),
        LocalDateTime.now(),
        action
    );
  }
}
