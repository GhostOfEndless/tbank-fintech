package com.example.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class CurrencyNotFoundException extends RuntimeException {

    private final String currencyCode;

    public CurrencyNotFoundException(String currencyCode) {
        super("currency.code.not_found");
        this.currencyCode = currencyCode;
    }
}
