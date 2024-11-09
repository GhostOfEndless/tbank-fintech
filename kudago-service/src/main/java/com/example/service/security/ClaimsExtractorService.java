package com.example.service.security;

import com.example.config.secutiry.JwtProperties;
import com.example.exception.entity.TokenNotFoundException;
import com.example.repository.security.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ClaimsExtractorService {

    private final JwtProperties jwtProperties;
    private final TokenRepository tokenRepository;

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenRevoked(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(TokenNotFoundException::new)
                .isRevoked();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, @NonNull Function<Claims, T> claimsResolver) {
        var claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(jwtProperties.getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
