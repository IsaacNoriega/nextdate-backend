package com.nextdate.backend.auth.application.login;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nextdate.backend.auth.application.login.LoginUserUseCase.LoginCommand;
import com.nextdate.backend.auth.application.login.LoginUserUseCase.LoginResult;
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
class LoginUserServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private TokenService tokenService;

  @Mock private PasswordEncoder passwordEncoder;

  private LoginUserService loginUserService;

  @BeforeEach
  void setUp() {
    loginUserService = new LoginUserService(userRepository, tokenService, passwordEncoder);
  }

  @Test
  @DisplayName("1. Happy Path: Debería iniciar sesión correctamente con credenciales válidas")
  void deberiaIniciarSesionCorrectamente() {
    // Arrange
    String email = "test@nextdate.com";
    String rawPassword = "Password123!";
    String encodedPassword = "encoded_password_hash";
    String expectedToken = "jwt.fake.token";
    UUID userId = UUID.randomUUID();

    LoginCommand command = new LoginCommand(email, rawPassword);
    User user =
        User.builder().id(userId).email(email).passwordHash(encodedPassword).active(true).build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);
    when(tokenService.generateToken(user)).thenReturn(expectedToken);

    // Act
    LoginResult result = loginUserService.login(command);

    // Assert
    assertNotNull(result, "El resultado del login no debe ser nulo");
    assertEquals(user, result.user(), "El usuario retornado debe ser el mismo que el encontrado");
    assertEquals(expectedToken, result.token(), "El token retornado debe ser el esperado");

    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).matches(rawPassword, encodedPassword);
    verify(tokenService, times(1)).generateToken(user);
  }

  @Test
  @DisplayName(
      "2 & 3. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el usuario no existe")
  void deberiaLanzarExcepcionCuandoUsuarioNoExiste() {
    // Arrange
    String email = "noexiste@nextdate.com";
    LoginCommand command = new LoginCommand(email, "password");

    when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> loginUserService.login(command),
            "Debe lanzar IllegalArgumentException al no encontrar el usuario");

    assertEquals("Usuario o contraseña incorrecta", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verifyNoInteractions(passwordEncoder);
    verifyNoInteractions(tokenService);
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando la contraseña es incorrecta")
  void deberiaLanzarExcepcionCuandoContrasenaEsIncorrecta() {
    // Arrange
    String email = "test@nextdate.com";
    String wrongPassword = "wrongPassword";
    String encodedPassword = "encoded_password_hash";

    LoginCommand command = new LoginCommand(email, wrongPassword);
    User user =
        User.builder()
            .id(UUID.randomUUID())
            .email(email)
            .passwordHash(encodedPassword)
            .active(true)
            .build();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(passwordEncoder.matches(wrongPassword, encodedPassword)).thenReturn(false);

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> loginUserService.login(command),
            "Debe lanzar IllegalArgumentException al fallar la validación del hash");

    assertEquals("Usuario o contraseña incorrectos", exception.getMessage());
    verify(userRepository, times(1)).findByEmail(email);
    verify(passwordEncoder, times(1)).matches(wrongPassword, encodedPassword);
    verifyNoInteractions(tokenService);
  }
}
