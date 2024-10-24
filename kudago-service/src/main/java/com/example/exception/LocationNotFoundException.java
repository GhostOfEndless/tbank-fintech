package com.example.exception;

public class LocationNotFoundException extends EntityNotFoundException {

    public LocationNotFoundException(Long id) {
        super("location.not_found", id);
    }
}
