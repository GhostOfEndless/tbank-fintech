package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;
import lombok.Getter;

@Getter
public abstract class RelatedEntityNotFoundException extends ApplicationRuntimeException {

  private final Long id;

  public RelatedEntityNotFoundException(String message, Long id) {
    super(message);
    this.id = id;
  }
}
