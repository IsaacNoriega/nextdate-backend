package com.nextdate.backend.auth.application.recovery;

import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordService implements ResetPasswordUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public ResetPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void reset(ResetCommand command) {

    // Buscar usuario con el token proporcionado
    User user =
        userRepository
            .findByResetPasswordToken(command.token())
            .orElseThrow(() -> new IllegalArgumentException("Token invalido o expirado"));

    // Validar que el token no haya expirado
    if (user.getResetPasswordExpires().isBefore(Instant.now())) {
      throw new IllegalArgumentException("Token expirado");
    }

    // Hashear la nueva contraseña
    String newPasswordHash = passwordEncoder.encode(command.newPassword());

    // Actualizar usuario con la nueva contraseña y limpiar el token de recuperacion
    User updatedUser = user.withPassword(newPasswordHash);
    userRepository.save(updatedUser);
  }
}
