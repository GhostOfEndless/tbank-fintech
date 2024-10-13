package com.example.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class CurrencyServiceUnavailableException extends ApplicationRuntimeException {

    public CurrencyServiceUnavailableException() {
        super("currency.service.unavailable");
    }
}
