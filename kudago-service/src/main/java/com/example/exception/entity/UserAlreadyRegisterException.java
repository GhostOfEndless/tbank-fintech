package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;
import lombok.Getter;

@Getter
public class UserAlreadyRegisterException extends ApplicationRuntimeException {

  private final String login;

  public UserAlreadyRegisterException(String login) {
    super("username.already_register");
    this.login = login;
  }
}
