package com.example.service.observer.location;

import com.example.entity.CrudAction;
import com.example.entity.Location;

public interface LocationObserver {

    void onLocationChanged(Location location, CrudAction action);
}
