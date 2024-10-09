package com.example.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class InvalidCurrencyCodeException extends RuntimeException {

    private final String currencyCode;

    public InvalidCurrencyCodeException(String currencyCode) {
        super("currency.code.invalid");
        this.currencyCode = currencyCode;
    }
}
