package com.example.client;

import com.example.entity.Item;
import com.example.entity.ValCurs;
import com.example.entity.Valuta;
import com.example.entity.Valute;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class CBRService {

    private final RestClient restClient;

    @Cacheable("val-curs")
    @Retry(name = "cbr-client")
    @CircuitBreaker(name = "cbr-client", fallbackMethod = "getValCursFallback")
    public Optional<ValCurs> getValCurs() {
        var response = restClient.get()
                .uri("/XML_daily.asp")
                .retrieve()
                .toEntity(ValCurs.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            response.getBody().getValutes().add(getRUBCurs());
        }

        log.debug("Response from CBR: {}", response);

        return Optional.ofNullable(response.getBody());
    }

    @Cacheable("valuta")
    @Retry(name = "cbr-client")
    @CircuitBreaker(name = "cbr-client", fallbackMethod = "getValutaFallback")
    public Optional<Valuta> getValuta() {
        var response = restClient.get()
                .uri("/XML_valFull.asp")
                .retrieve()
                .toEntity(Valuta.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            response.getBody().getItems().add(getRUB());
        }

        log.debug("Response from CBR: {}", response);

        return Optional.ofNullable(response.getBody());
    }

    @SuppressWarnings("unused")
    private Optional<ValCurs> getValCursFallback(Exception e) {
        log.error("Circuit breaker fallback for getValCurs. Error: {}", e.getMessage());
        return Optional.empty();
    }

    @SuppressWarnings("unused")
    private Optional<Valuta> getValutaFallback(Exception e) {
        log.error("Circuit breaker fallback for getValuta. Error: {}", e.getMessage());
        return Optional.empty();
    }

    private Item getRUB() {
        return Item.builder()
                .id("R01000")
                .parentCode("R01000")
                .isoNumCode(643)
                .isoCharCode("RUB")
                .name("Российский рубль")
                .engName("Russian ruble")
                .nominal(1)
                .build();
    }

    private Valute getRUBCurs() {
        return Valute.builder()
                .id("R01000")
                .charCode("RUB")
                .name("Российский рубль")
                .numCode(100)
                .value(1.0f)
                .nominal(1)
                .vunitRate(1.0f)
                .build();
    }
}
