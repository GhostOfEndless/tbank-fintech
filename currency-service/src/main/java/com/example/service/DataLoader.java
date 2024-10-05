package com.example.service;

import com.example.client.CBRService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader {

    private final CBRService cbrService;

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        var response = cbrService.getCurrencies();
        log.info("Response is: {}", response);
    }
}
