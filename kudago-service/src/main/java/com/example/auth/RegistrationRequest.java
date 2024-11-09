package com.example.auth;

import lombok.Builder;

@Builder
public record RegistrationRequest(
        String displayName,
        String login,
        String password
) {
}
