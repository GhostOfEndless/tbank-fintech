package com.example.service;

import com.example.aspect.LogExecutionTime;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationListener<ContextRefreshedEvent> {

    private final CategoryService categoryService;
    private final LocationService locationService;

    @LogExecutionTime
    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        categoryService.init();
        locationService.init();
    }
}
