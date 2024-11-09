package com.example.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class InvalidCurrencyException extends ApplicationRuntimeException {

  private final String currency;

  public InvalidCurrencyException(String currency) {
    super("request.param.invalid_currency");
    this.currency = currency;
  }
}
