package com.example.exception.entity;

public class CategoryExistsException extends SlugAlreadyExistsException {

  public CategoryExistsException(String slug) {
    super("category.slug.exists", slug);
  }
}
