package com.example.entity.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Entity
@Table(name = "t_tokens", schema = "security")
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tokens_seq_generator")
    @SequenceGenerator(name = "tokens_seq_generator", sequenceName = "security.tokens_seq")
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "c_token", nullable = false)
    private String token;

    @Column(name = "c_revoked", nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "c_app_user_id", nullable = false)
    private AppUser appUser;
}
