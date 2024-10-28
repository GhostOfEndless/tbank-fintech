package com.example.bootstrap.command.impl;

import com.example.bootstrap.command.DataInitializationCommand;
import com.example.service.EventService;
import com.example.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocationInitializationCommand implements DataInitializationCommand {

    private final LocationService locationService;
    private final EventService eventService;

    @Override
    public void execute() {
        log.info("Executing location initialization command");
        locationService.init();
        eventService.init();
    }
}
