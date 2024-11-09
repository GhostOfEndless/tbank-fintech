package com.example.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class ApplicationRuntimeException extends RuntimeException {

  public ApplicationRuntimeException(String message) {
    super(message);
  }
}
