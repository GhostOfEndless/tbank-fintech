package com.example.auth;

public record AuthenticationRequest(
        String login,
        String password,
        boolean rememberMe
) {
}