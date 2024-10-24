package com.example.exception.entity;

import com.example.exception.ApplicationRuntimeException;
import lombok.Getter;

@Getter
public abstract class EntityNotFoundException extends ApplicationRuntimeException {

    private final Long id;

    public EntityNotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
