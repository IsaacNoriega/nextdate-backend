package com.nextdate.backend.auth.application;

import com.nextdate.backend.auth.domain.User; // Entidad
import com.nextdate.backend.auth.domain.UserRepository; // Repositorio (Interfaz)
import java.util.UUID; // Para generar el ID del usuario
import org.springframework.security.crypto.password.PasswordEncoder; // Para encriptar la contraseña
import org.springframework.stereotype.Service; // Indica que esta es una clase de servicio

// Clase que implementa la interfaz RegisterUserUseCase
@Service
public class RegisterUserService implements RegisterUserUseCase {

  private final UserRepository userRepository; // Repositorio (Inyectado por Spring)
  private final PasswordEncoder
      passwordEncoder; // Codificador de contraseñas (Inyectado por Spring)

  // Inyección de dependencias (Constructor)
  public RegisterUserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  // Método que implementa el caso de uso de registro de usuario
  @Override
  public User register(RegisterUserCommand command) {

    // verificar si el usuario ya esta registrado
    if (userRepository.findByEmail(command.email()).isPresent()) {
      throw new IllegalArgumentException("El correo ya esta registrado");
    }

    // Crear el usuario
    User newUser =
        User.builder()
            .id(UUID.randomUUID())
            .email(command.email())
            .passwordHash(passwordEncoder.encode(command.password())) // Codificar la contraseña
            .active(true)
            .build();

    // Guardar el usuario
    return userRepository.save(newUser);
  }
}
