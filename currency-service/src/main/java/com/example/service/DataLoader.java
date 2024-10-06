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
        cbrService.getValCurs().ifPresentOrElse(
                valCurs -> log.info("Loaded '{}' valute curse entries", valCurs.getValutes().size()),
                () -> log.warn("Error occurred while loading valute curse entries!"));

        cbrService.getValuta().ifPresentOrElse(
                valCurs -> log.info("Loaded '{}' valute symbols", valCurs.getItems().size()),
                () -> log.warn("Error occurred while loading valute symbols!"));
    }
}
