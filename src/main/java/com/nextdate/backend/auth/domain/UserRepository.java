package com.nextdate.backend.auth.domain;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  Optional<User> findById(UUID id);

  Optional<User> findByEmail(String email);

  Optional<User> findByResetPasswordToken(String token);

  User save(User user);
}
