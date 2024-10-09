package com.example.service;

import com.example.client.CBRService;
import com.example.entity.ValCurs;
import com.example.exception.CurrencyNotFoundException;
import com.example.exception.InvalidCurrencyCodeException;
import com.example.exception.ServiceUnavailableException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final CBRService cbrService;

    public float getCurrencyRate(String currencyCode) {
        var currencies = cbrService.getValCurs().orElseThrow(ServiceUnavailableException::new);

        validateCurrencyCode(currencyCode);

        return getCurrencyRateFromList(currencies, currencyCode);
    }

    public float convertToCurrency(String fromCurrency, String toCurrency, float amount) {
        var currencies = cbrService.getValCurs().orElseThrow(ServiceUnavailableException::new);

        validateCurrencyCode(fromCurrency);
        validateCurrencyCode(toCurrency);

        var fromCurrencyRate = getCurrencyRateFromList(currencies, fromCurrency);
        var toCurrencyRate = getCurrencyRateFromList(currencies, toCurrency);

        return amount * fromCurrencyRate / toCurrencyRate;
    }

    private void validateCurrencyCode(String currencyCode) {
        var currency = cbrService.getValuta().orElseThrow(ServiceUnavailableException::new);

        currency.getItems().stream()
                .filter(item -> item.getIsoCharCode().equals(currencyCode))
                .findFirst()
                .orElseThrow(() ->
                        new InvalidCurrencyCodeException(currencyCode));
    }

    private float getCurrencyRateFromList(@NotNull ValCurs currencies, String targetCurrencyCode) {
        return currencies.getValutes().stream()
                .filter(x -> x.getCharCode().equals(targetCurrencyCode))
                .findFirst().orElseThrow(() ->
                        new CurrencyNotFoundException(targetCurrencyCode))
                .getVunitRate();
    }
}
