package com.example.service.observer.location;

import com.example.entity.CrudAction;
import com.example.entity.Location;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationPublisher {

  private final List<LocationObserver> observers;

  public void notifyObservers(Location location, CrudAction action) {
    observers.forEach(observer -> observer.onLocationChanged(location, action));
  }
}
