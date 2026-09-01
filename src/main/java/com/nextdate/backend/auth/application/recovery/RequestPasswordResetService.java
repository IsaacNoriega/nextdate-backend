package com.nextdate.backend.auth.application.recovery;

import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import java.time.Instant; // clase para manejar tiempo absoluto
import java.time.temporal.ChronoUnit; // clase para operaciones con unidades de tiempo
import java.util.UUID; // clase para generar identificadores únicos
import org.slf4j.Logger; // clase para manejar logs
import org.slf4j.LoggerFactory; // clase para crear instances de Logger
import org.springframework.stereotype.Service; // clase para indicar que esta clase es un Service

@Service
public class RequestPasswordResetService implements RequestPasswordResetUseCase {

  private static final Logger log =
      LoggerFactory.getLogger(RequestPasswordResetService.class); // logger para manejo de logs
  private final UserRepository userRepository; // interfaz para el repositorio de usuarios

  // Constructor para inyectar el repositorio de usuarios
  public RequestPasswordResetService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public void requestReset(String email) {

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () ->
                    new IllegalArgumentException(
                        "Usuario no encontrado")); // lanzar excepcion si no se encuentra el usuario

    String token = UUID.randomUUID().toString(); // generar token unico
    Instant expires = Instant.now().plus(15, ChronoUnit.MINUTES); // generar fecha de expiracion

    User updateUser =
        user.withResetPasswordToken(
            token, expires); // actualizar usuario con token y fecha de expiracion
    userRepository.save(updateUser); // guardar usuario

    log.info("Solicitud de recuperación de contraseña recibida para: {}", email);
    log.debug("Token de recuperación generado: {}, expiración: {}", token, expires);
  }
}
