package com.nextdate.backend.auth.application.recovery;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class ResetPasswordServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  private ResetPasswordService resetPasswordService;

  @BeforeEach
  void setUp() {
    resetPasswordService = new ResetPasswordService(userRepository, passwordEncoder);
  }

  @Test
  @DisplayName(
      "1. Happy Path: Debería reestablecer la contraseña si el token es válido y no ha expirado")
  void deberiaRestablecerContrasenaCorrectamente() {
    // Arrange
    String validToken = "valid-reset-token-123";
    String newPassword = "NewPassword123!";
    String encodedNewPassword = "encoded_new_password_hash";

    ResetPasswordUseCase.ResetCommand command =
        new ResetPasswordUseCase.ResetCommand(validToken, newPassword);

    User user =
        User.builder()
            .id(UUID.randomUUID())
            .email("test@nextdate.com")
            .passwordHash("old_hash")
            .active(true)
            .resetPasswordToken(validToken)
            .resetPasswordExpires(Instant.now().plus(15, ChronoUnit.MINUTES))
            .build();

    when(userRepository.findByResetPasswordToken(validToken)).thenReturn(Optional.of(user));
    when(passwordEncoder.encode(newPassword)).thenReturn(encodedNewPassword);

    ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

    // Act
    resetPasswordService.reset(command);

    // Assert
    verify(userRepository, times(1)).save(userCaptor.capture());
    User updatedUser = userCaptor.getValue();

    assertEquals(encodedNewPassword, updatedUser.getPasswordHash());
    assertNull(updatedUser.getResetPasswordToken(), "El token debe limpiarse tras usarse");
    assertNull(updatedUser.getResetPasswordExpires(), "La expiración debe limpiarse tras usarse");
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el token no existe")
  void deberiaLanzarExcepcionCuandoTokenNoExiste() {
    // Arrange
    String invalidToken = "token-inexistente";
    ResetPasswordUseCase.ResetCommand command =
        new ResetPasswordUseCase.ResetCommand(invalidToken, "password");

    when(userRepository.findByResetPasswordToken(invalidToken)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> resetPasswordService.reset(command));

    assertEquals("Token invalido o expirado", exception.getMessage());
    verify(userRepository, never()).save(any());
  }

  @Test
  @DisplayName(
      "3, 4 & 5. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el token ya expiró")
  void deberiaLanzarExcepcionCuandoTokenHaExpirado() {
    // Arrange
    String expiredToken = "expired-token-123";
    ResetPasswordUseCase.ResetCommand command =
        new ResetPasswordUseCase.ResetCommand(expiredToken, "NewPassword123!");

    User userWithExpiredToken =
        User.builder()
            .id(UUID.randomUUID())
            .email("test@nextdate.com")
            .resetPasswordToken(expiredToken)
            .resetPasswordExpires(Instant.now().minus(5, ChronoUnit.MINUTES)) // Expirado hace 5 min
            .build();

    when(userRepository.findByResetPasswordToken(expiredToken))
        .thenReturn(Optional.of(userWithExpiredToken));

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> resetPasswordService.reset(command));

    assertEquals("Token expirado", exception.getMessage());
    verify(passwordEncoder, never()).encode(any());
    verify(userRepository, never()).save(any());
  }
}
