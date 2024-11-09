package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;
import lombok.Getter;

@Getter
public abstract class SlugAlreadyExistsException extends ApplicationRuntimeException {

  private final String slug;

  public SlugAlreadyExistsException(String message, String slug) {
    super(message);
    this.slug = slug;
  }
}
