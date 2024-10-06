package com.example.client;

import com.example.entity.Item;
import com.example.entity.ValCurs;
import com.example.entity.Valuta;
import com.example.entity.Valute;
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
    public Optional<ValCurs> getValCurs() {
        var response = restClient.get()
                .uri("/XML_daily.asp")
                .retrieve()
                .toEntity(ValCurs.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            response.getBody().getValutes().add(getRUBCurs());
        }

        log.info(response.toString());

        return Optional.ofNullable(response.getBody());
    }

    @Cacheable("valuta")
    public Optional<Valuta> getValuta() {
        var response = restClient.get()
                .uri("/XML_valFull.asp")
                .retrieve()
                .toEntity(Valuta.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            response.getBody().getItems().add(getRUB());
        }

        log.info(response.toString());

        return Optional.ofNullable(response.getBody());
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
