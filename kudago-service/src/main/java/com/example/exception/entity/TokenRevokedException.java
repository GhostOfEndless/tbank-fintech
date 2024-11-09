package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;

public class TokenRevokedException extends ApplicationRuntimeException {

  public TokenRevokedException() {
    super("token.revoked");
  }
}
