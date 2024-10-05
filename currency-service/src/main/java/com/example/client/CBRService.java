package com.example.client;

import com.example.entity.ValCurs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Service
@EnableCaching
@RequiredArgsConstructor
public class CBRService {

    private final RestClient restClient;

    @Cacheable("currencies")
    public Optional<ValCurs> getCurrencies() {
        ResponseEntity<ValCurs> response = restClient.get()
                .retrieve()
                .toEntity(ValCurs.class);
        log.info(response.toString());
        return Optional.ofNullable(response.getBody());
    }
}
