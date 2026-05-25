package com.nextdate.backend.auth.application.login;

import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService implements LoginUserUseCase {

  // Implementación de dependencias para inyección de dependencias
  private final UserRepository userRepository;
  private final TokenService tokenService;
  private final PasswordEncoder passwordEncoder;

  // Constructor para inyectar dependencias
  public LoginUserService(
      UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.tokenService = tokenService;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public LoginResult login(LoginCommand command) {

    // Buscamos por email
    User user =
        userRepository
            .findByEmail(command.email())
            .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña incorrecta"));

    // Verificamos contraseña
    if (!passwordEncoder.matches(command.password(), user.getPasswordHash())) {
      throw new IllegalArgumentException("Usuario o contraseña incorrectos");
    }

    // Creacion del Token
    String token = tokenService.generateToken(user);

    // Devolvemos al usuario y el token
    return new LoginResult(user, token);
  }
}
