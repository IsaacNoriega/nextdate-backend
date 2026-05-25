package com.nextdate.backend.auth.domain;

import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class User {
  private final UUID id;
  private final String email;
  private final String passwordHash;
  private final boolean active;
  private final String resetPasswordToken;
  private final Instant resetPasswordExpires;

  // Regla de negocio en el dominio (Ejemplo)
  public User deactivate() {
    return User.builder()
        .id(this.id)
        .email(this.email)
        .passwordHash(this.passwordHash)
        .active(false)
        .resetPasswordToken(this.resetPasswordToken)
        .resetPasswordExpires(this.resetPasswordExpires)
        .build();
  }

  public User withResetPasswordToken(String token, Instant expires) {
    return User.builder()
        .id(this.id)
        .email(this.email)
        .passwordHash(this.passwordHash)
        .active(this.active)
        .resetPasswordToken(token)
        .resetPasswordExpires(expires)
        .build();
  }

  public User withPassword(String newPasswordHash) {
    return User.builder()
        .id(this.id)
        .email(this.email)
        .passwordHash(newPasswordHash)
        .active(this.active)
        .resetPasswordToken(null)
        .resetPasswordExpires(null)
        .build();
  }
}
