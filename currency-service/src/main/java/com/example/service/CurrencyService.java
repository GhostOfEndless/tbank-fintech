package com.example.service;

import com.example.client.CBRService;
import com.example.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CBRService cbrService;

    public float getCurrencyRate(String currencyCode) {
        var currencies = cbrService.getCurrencies().orElseThrow(() ->
                new ServiceUnavailableException("Service unavailable"));

        var currency = currencies.getValutes().stream()
                .filter(x -> x.getCharCode().equals(currencyCode))
                .findFirst();

        return currency.orElseThrow(() ->
                        new NoSuchElementException("No such currency"))
                .getVunitRate();
    }
}
