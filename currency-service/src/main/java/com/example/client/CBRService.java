package com.example.client;

import com.example.entity.ValCurs;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CBRService {

    private final RestClient restClient;

    @CircuitBreaker(name = "cbrService", fallbackMethod = "com.")
    public Optional<ValCurs> getCurrencies() {
        ResponseEntity<ValCurs> response = restClient.delete()
                .retrieve()
                .toEntity(ValCurs.class);
        log.info(response.toString());
        return Optional.ofNullable(response.getBody());
    }

    public Optional<ValCurs> fallbackGetCurrencies(Throwable ex) {
        log.error("Ошибка при вызове API ЦБР, возвращаем запасное значение: {}", ex.getMessage());
        return Optional.empty();
    }
}
