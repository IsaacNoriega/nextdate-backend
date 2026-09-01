package com.nextdate.backend.auth.application.register;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextdate.backend.auth.application.register.RegisterUserUseCase.RegisterUserCommand;
import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private RegisterUserService registerUserService;

  @BeforeEach
  void setUp() {
    registerUserService = new RegisterUserService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName("1. Happy Path: Debería registrar un nuevo usuario correctamente")
  void deberiaRegistrarUsuarioCorrectamente() {
    // Arrange
    String email = "nuevo@nextdate.com";
    String rawPassword = "Password123!";
    String encodedPassword = "encoded_password_hash";

    RegisterUserCommand command = new RegisterUserCommand(email, rawPassword);

    User savedUser =
        User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .passwordHash(encodedPassword)
            .active(true)
            .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    // Act
    User result = registerUserService.register(command);

    // Assert
    assertNotNull(result, "El usuario retornado no debe ser nulo");
    assertEquals(email, result.getEmail(), "El correo debe coincidir");
    assertEquals(encodedPassword, result.getPasswordHash(), "La contraseña debe estar codificada");
    assertTrue(result.isActive(), "El usuario debe estar activo por defecto");

    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).encode(rawPassword);
    verify(userRepository, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el correo ya existe")
  void deberiaLanzarExcepcionCuandoCorreoYaEstaRegistrado() {
    // Arrange
    String email = "existente@nextdate.com";
    RegisterUserCommand command = new RegisterUserCommand(email, "password123");

    User existingUser =
        User.builder().id(UUID.randomUUID()).email(email).passwordHash("hash").active(true).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> registerUserService.register(command),
            "Debe lanzar IllegalArgumentException si el correo está registrado");

    assertEquals("El correo ya esta registrado", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verifyNoInteractions(passwordEncoder);
    verify(userRepository, never()).save(any(User.class));
  }
}
