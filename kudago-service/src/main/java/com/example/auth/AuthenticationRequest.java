package com.example.auth;

import lombok.Builder;

@Builder
public record AuthenticationRequest(
        String login,
        String password,
        boolean rememberMe
) {
}