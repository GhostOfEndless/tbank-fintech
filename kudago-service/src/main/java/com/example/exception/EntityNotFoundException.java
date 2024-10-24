package com.example.exception;

import lombok.Getter;

@Getter
public abstract class EntityNotFoundException extends ApplicationRuntimeException {

    private final Long id;

    public EntityNotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
