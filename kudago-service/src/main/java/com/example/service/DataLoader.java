package com.example.service;

import com.example.aspect.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final CategoryService categoryService;
    private final LocationService locationService;

    @LogExecutionTime
    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        categoryService.init();
        locationService.init();
    }
}
