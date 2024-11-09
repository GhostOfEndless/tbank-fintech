package com.example.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class DateBoundsException extends ApplicationRuntimeException {

  public DateBoundsException() {
    super("request.param.date_bound_invalid");
  }
}
