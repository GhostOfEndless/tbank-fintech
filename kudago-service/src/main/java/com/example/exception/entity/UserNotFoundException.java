package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends ApplicationRuntimeException {

  private final String login;

  public UserNotFoundException(String login) {
    super("username.not_found");
    this.login = login;
  }
}
