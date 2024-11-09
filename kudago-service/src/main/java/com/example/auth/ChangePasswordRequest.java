package com.example.auth;

import lombok.Builder;

@Builder
public record ChangePasswordRequest(
    String newPassword,
    String twoFactorCode
) {
}
