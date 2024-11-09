package com.example.exception.entity;

public class CategoryNotFoundException extends EntityNotFoundException {

  public CategoryNotFoundException(Long id) {
    super("category.not_found", id);
  }
}
