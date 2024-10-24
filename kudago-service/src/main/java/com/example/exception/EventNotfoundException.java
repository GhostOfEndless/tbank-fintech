package com.example.exception;

public class EventNotfoundException extends EntityNotFoundException {

    public EventNotfoundException(Long id) {
        super("event.not_found", id);
    }
}
