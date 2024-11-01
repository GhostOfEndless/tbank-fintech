package com.example.auth;

public record RegistrationRequest(
        String displayName,
        String login,
        String password
) {
}
