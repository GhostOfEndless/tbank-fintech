package com.example.repository.security;

import com.example.entity.security.AppUser;
import com.example.entity.security.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    List<Token> findAllByAppUserAndRevoked(AppUser appUser, boolean revoked);
}
