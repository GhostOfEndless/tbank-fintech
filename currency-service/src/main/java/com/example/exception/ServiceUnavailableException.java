package com.example.exception;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=false)
public class ServiceUnavailableException extends RuntimeException {

    public ServiceUnavailableException(String message) {
        super(message);
    }
}
