package com.nextdate.backend.auth.application;

import com.nextdate.backend.auth.domain.User;

// Record para el comando de registro de usuario (DTO de entrada)
public interface RegisterUserUseCase {
  User register(RegisterUserCommand command);

  record RegisterUserCommand(String email, String password) {}
}
