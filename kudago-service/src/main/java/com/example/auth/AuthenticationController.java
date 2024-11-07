package com.example.auth;

import com.example.service.security.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/api/v1/auth/authenticate")
    public AuthenticationResponse authenticate(@RequestBody AuthenticationRequest request) {
        return authenticationService.authenticate(request);
    }

    @PostMapping("/api/v1/auth/register")
    public AuthenticationResponse register(@RequestBody RegistrationRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/api/v1/auth/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        authenticationService.logout(authentication);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/api/v1/auth/change-password")
    public ResponseEntity<Void> changePassword(@RequestBody ChangePasswordRequest request,
                                               Authentication authentication) {
        authenticationService.changePassword(authentication, request);
        return ResponseEntity.ok().build();
    }
}
