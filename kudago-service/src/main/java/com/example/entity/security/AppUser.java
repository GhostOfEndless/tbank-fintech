package com.example.entity.security;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Builder
@Setter
@Getter
@EqualsAndHashCode(exclude = "id")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "t_app_users", schema = "security")
public class AppUser implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_users_seq_generator")
  @SequenceGenerator(name = "app_users_seq_generator", sequenceName = "security.app_users_seq")
  @Column(unique = true, nullable = false)
  private Long id;

  @Column(name = "c_display_name", nullable = false)
  private String displayName;

  @Column(name = "c_login", unique = true, nullable = false)
  private String login;

  @Column(name = "c_hashed_password", nullable = false)
  private String hashedPassword;

  @Enumerated(EnumType.STRING)
  @Column(name = "c_role", nullable = false)
  private Role role;

  @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Token> tokens;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  @Override
  public String getPassword() {
    return hashedPassword;
  }

  @Override
  public String getUsername() {
    return login;
  }
}
