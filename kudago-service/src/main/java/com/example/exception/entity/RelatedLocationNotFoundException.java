package com.example.exception.entity;

public class RelatedLocationNotFoundException extends RelatedEntityNotFoundException {

  public RelatedLocationNotFoundException(Long id) {
    super("location.not_found", id);
  }
}
