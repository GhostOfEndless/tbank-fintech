package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;

public class TokenNotFoundException extends ApplicationRuntimeException {

  public TokenNotFoundException() {
    super("token.not_found");
  }
}
