package com.example.service.security;

import com.example.config.secutiry.JwtProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        return generateToken(Map.of(), userDetails, rememberMe);
    }

    public String generateToken(Map<String, Object> extraClaims,
                                @NonNull UserDetails userDetails, boolean rememberMe) {
        var ttl = rememberMe ? jwtProperties.longTtl() : jwtProperties.ttl();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ttl.toMillis()))
                .signWith(jwtProperties.getSignInKey(), Jwts.SIG.HS256)
                .compact();
    }
}
