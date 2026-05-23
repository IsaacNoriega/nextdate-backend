package com.nextdate.backend.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class User {
    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final boolean active;

    // Regla de negocio en el dominio (Ejemplo)
    public User deactivate() {
        return User.builder()
                .id(this.id)
                .email(this.email)
                .passwordHash(this.passwordHash)
                .active(false)
                .build();
    }
}
