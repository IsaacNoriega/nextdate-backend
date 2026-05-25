package com.nextdate.backend.auth.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Definir el repositorio de usuarios
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
  Optional<UserJpaEntity> findByEmail(String email);

  Optional<UserJpaEntity> findByResetPasswordToken(String resetPasswordToken);
}
