package com.example.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ServiceUnavailableException extends ApplicationRuntimeException {

  public ServiceUnavailableException() {
    super("service.unavailable");
  }
}
