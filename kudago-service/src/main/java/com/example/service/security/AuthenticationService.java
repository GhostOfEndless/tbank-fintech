package com.example.service.security;

import com.example.auth.AuthenticationRequest;
import com.example.auth.AuthenticationResponse;
import com.example.auth.ChangePasswordRequest;
import com.example.auth.RegistrationRequest;
import com.example.entity.security.AppUser;
import com.example.entity.security.Role;
import com.example.entity.security.Token;
import com.example.exception.entity.InvalidTwoFactorCodeException;
import com.example.exception.entity.UserAlreadyRegisterException;
import com.example.exception.entity.UserNotFoundException;
import com.example.repository.security.AppUserRepository;
import com.example.repository.security.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                )
        );
        AppUser user = appUserRepository.findByLogin(request.login())
                .orElseThrow(() -> new UserNotFoundException(request.login()));

        var tokens = tokenRepository.findAllByAppUserAndRevoked(user, false);
        tokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(tokens);

        String jwtToken = jwtService.generateToken(user, request.rememberMe());
        Token token = Token.builder()
                .token(jwtToken)
                .appUser(user)
                .build();
        tokenRepository.save(token);
        return new AuthenticationResponse(jwtToken);
    }

    public AuthenticationResponse register(@NonNull RegistrationRequest request) {
        appUserRepository.findByLogin(request.login())
                .ifPresent(appUser -> {
                    throw new UserAlreadyRegisterException(appUser.getLogin());
                });

        AppUser user = AppUser.builder()
                .displayName(request.displayName())
                .login(request.login())
                .role(Role.USER)
                .hashedPassword(passwordEncoder.encode(request.password()))
                .build();
        appUserRepository.save(user);

        String jwtToken = jwtService.generateToken(user, false);
        Token token = Token.builder()
                .token(jwtToken)
                .appUser(user)
                .build();
        tokenRepository.save(token);
        return new AuthenticationResponse(jwtToken);
    }

    public void logout(@NonNull Authentication authentication) {
        var userDetails = (UserDetails) authentication.getPrincipal();

        AppUser user = appUserRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        var tokens = tokenRepository.findAllByAppUserAndRevoked(user, false);
        tokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(tokens);
    }

    public void changePassword(@NonNull Authentication authentication, @NonNull ChangePasswordRequest request) {
        if (!request.twoFactorCode().equals("0000")) {
            throw new InvalidTwoFactorCodeException();
        }

        var userDetails = (UserDetails) authentication.getPrincipal();

        AppUser user = appUserRepository.findByLogin(userDetails.getUsername())
                .orElseThrow(() -> new UserNotFoundException(userDetails.getUsername()));

        user.setHashedPassword(passwordEncoder.encode(request.newPassword()));
        appUserRepository.save(user);
    }
}
