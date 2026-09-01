package com.nextdate.backend.auth.infrastructure.security;

import static org.junit.jupiter.api.Assertions.*;

import com.nextdate.backend.auth.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenServiceTest {

  private static final String SECRET_KEY =
      "test_super_secret_key_with_at_least_64_characters_for_hmac512_hash_security_test";
  private static final long EXPIRATION_MS = 3600000; // 1 hora

  private JwtTokenService jwtTokenService;

  @BeforeEach
  void setUp() {
    jwtTokenService = new JwtTokenService(SECRET_KEY, EXPIRATION_MS);
  }

  @Test
  @DisplayName("Debería generar un token JWT válido con el ID y email del usuario")
  void deberiaGenerarYValidarTokenCorrectamente() {
    UUID userId = UUID.randomUUID();
    String email = "test@nextdate.com";
    User user = User.builder().id(userId).email(email).passwordHash("hash").active(true).build();

    String token = jwtTokenService.generateToken(user);

    assertNotNull(token);
    assertFalse(token.isBlank());

    Optional<UUID> extractedUserId = jwtTokenService.extractUserId(token);
    Optional<String> extractedEmail = jwtTokenService.extractEmail(token);

    assertTrue(extractedUserId.isPresent(), "El ID extraído debe estar presente");
    assertEquals(userId, extractedUserId.get());

    assertTrue(extractedEmail.isPresent(), "El email extraído debe estar presente");
    assertEquals(email, extractedEmail.get());
  }

  @Test
  @DisplayName("Debería retornar Optional.empty() para un token inválido o corrupto")
  void deberiaRechazarTokenInvalido() {
    String invalidToken = "invalid.token.structure";

    Optional<UUID> extractedUserId = jwtTokenService.extractUserId(invalidToken);
    Optional<String> extractedEmail = jwtTokenService.extractEmail(invalidToken);

    assertTrue(extractedUserId.isEmpty(), "Token inválido no debe devolver ID");
    assertTrue(extractedEmail.isEmpty(), "Token inválido no debe devolver Email");
  }

  @Test
  @DisplayName("Debería rechazar un token firmado con una clave secreta diferente")
  void deberiaRechazarTokenConFirmaInvalida() {
    JwtTokenService otherService =
        new JwtTokenService(
            "another_secret_key_different_from_original_one_for_test", EXPIRATION_MS);

    UUID userId = UUID.randomUUID();
    User user =
        User.builder()
            .id(userId)
            .email("hacker@test.com")
            .passwordHash("hash")
            .active(true)
            .build();
    String forgedToken = otherService.generateToken(user);

    Optional<UUID> extractedUserId = jwtTokenService.extractUserId(forgedToken);
    assertTrue(extractedUserId.isEmpty(), "Token con firma de otra clave debe ser rechazado");
  }

  @Test
  @DisplayName("Debería rechazar un token expirado")
  void deberiaRechazarTokenExpirado() {
    // 0 milisegundos de expiración
    JwtTokenService expiredService = new JwtTokenService(SECRET_KEY, -1000);

    UUID userId = UUID.randomUUID();
    User user =
        User.builder()
            .id(userId)
            .email("expired@test.com")
            .passwordHash("hash")
            .active(true)
            .build();
    String expiredToken = expiredService.generateToken(user);

    Optional<UUID> extractedUserId = jwtTokenService.extractUserId(expiredToken);
    assertTrue(extractedUserId.isEmpty(), "Token expirado debe ser rechazado");
  }
}
