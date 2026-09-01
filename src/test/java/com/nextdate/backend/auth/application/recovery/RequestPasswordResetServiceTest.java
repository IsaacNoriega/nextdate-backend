package com.nextdate.backend.auth.application.recovery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RequestPasswordResetServiceTest {

  @Mock private UserRepository userRepository;

  private RequestPasswordResetService requestPasswordResetService;

  @BeforeEach
  void setUp() {
    requestPasswordResetService = new RequestPasswordResetService(userRepository);
  }

  @Test
  @DisplayName("1. Happy Path: Debería generar el token de recuperación y actualizar el usuario")
  void deberiaSolicitarRecuperacionDeContrasenaCorrectamente() {
    // Arrange
    String email = "usuario@nextdate.com";
    User existingUser =
        User.builder().id(UUID.randomUUID()).email(email).passwordHash("hash").active(true).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    // Act
    requestPasswordResetService.requestReset(email);

    // Assert
    verify(userRepository, times(1)).save(userCaptor.capture());
    User updatedUser = userCaptor.getValue();

    assertNotNull(updatedUser.getResetPasswordToken(), "El token no debe ser nulo");
    assertNotNull(updatedUser.getResetPasswordExpires(), "La fecha de expiración no debe ser nula");
    assertTrue(
        updatedUser.getResetPasswordExpires().isAfter(java.time.Instant.now()),
        "La expiración debe estar en el futuro");
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el usuario no existe")
  void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
    // Arrange
    String email = "desconocido@nextdate.com";
    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> requestPasswordResetService.requestReset(email));

    assertEquals("Usuario no encontrado", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(userRepository, never()).save(any());
  }
}
