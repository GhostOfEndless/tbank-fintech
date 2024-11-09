package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;

public class InvalidTwoFactorCodeException extends ApplicationRuntimeException {

  public InvalidTwoFactorCodeException() {
    super("two-factor.code_invalid");
  }
}
