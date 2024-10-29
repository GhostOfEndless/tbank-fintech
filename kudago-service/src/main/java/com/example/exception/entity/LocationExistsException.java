package com.example.exception.entity;

public class LocationExistsException extends SlugAlreadyExistsException {

    public LocationExistsException(String slug) {
        super("location.slug.exists", slug);
    }
}
