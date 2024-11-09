package com.example.service.observer.location;

import com.example.entity.CrudAction;
import com.example.entity.Location;
import com.example.entity.history.LocationMemento;
import com.example.service.history.LocationHistoryCaretaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationObserverImpl implements LocationObserver {

  private final LocationHistoryCaretaker historyCaretaker;

  @Override
  public void onLocationChanged(Location location, CrudAction action) {
    historyCaretaker.saveSnapshot(LocationMemento.createSnapshot(location, action));
  }
}
