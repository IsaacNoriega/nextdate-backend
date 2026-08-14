package com.nextdate.backend.auth.infrastructure.graphql;

import com.nextdate.backend.auth.application.login.LoginUserUseCase;
import com.nextdate.backend.auth.application.login.LoginUserUseCase.LoginCommand;
import com.nextdate.backend.auth.application.recovery.RequestPasswordResetUseCase;
import com.nextdate.backend.auth.application.recovery.ResetPasswordUseCase;
import com.nextdate.backend.auth.application.register.RegisterUserUseCase;
import com.nextdate.backend.auth.application.register.RegisterUserUseCase.RegisterUserCommand;
import com.nextdate.backend.auth.domain.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class UserGraphQLController {

  private final RegisterUserUseCase
      registerUserUseCase; // Instancia del caso de uso de registro de usuarios
  private final LoginUserUseCase
      loginUserUseCase; // Instancia del caso de uso de inicio de sesión de usuarios
  private final RequestPasswordResetUseCase
      requestPasswordResetUseCase; // Instancia del caso de uso de solicitud de restablecimiento de
  // contraseña
  private final ResetPasswordUseCase
      resetPasswordUseCase; // Instancia del caso de uso de restablecimiento de contraseña

  // Constructor para la inyeccion de dependencias
  public UserGraphQLController(
      RegisterUserUseCase registerUserUseCase,
      LoginUserUseCase loginUserUseCase,
      RequestPasswordResetUseCase requestPasswordResetUseCase,
      ResetPasswordUseCase resetPasswordUseCase) {
    this.registerUserUseCase = registerUserUseCase;
    this.loginUserUseCase = loginUserUseCase;
    this.requestPasswordResetUseCase = requestPasswordResetUseCase;
    this.resetPasswordUseCase = resetPasswordUseCase;
  }

  // Mapea la mutacion de registro de usuarios
  @MutationMapping
  public User registerUser(@Argument RegisterUserInput input) {
    RegisterUserCommand command =
        new RegisterUserCommand(
            input.email, input.password); // Creacion del comando de registro de usuarios
    return registerUserUseCase.register(
        command); // Ejecucion del caso de uso de registro de usuarios
  }

  // Mapea la mutacion de inicio de sesión de usuarios
  @MutationMapping
  public LoginUserUseCase.LoginResult login(@Argument LoginInput input) {
    LoginCommand command =
        new LoginCommand(
            input.email, input.password); // Creacion del comando de inicio de sesión de usuarios
    return loginUserUseCase.login(
        command); // Ejecucion del caso de uso de inicio de sesión de usuarios
  }

  // Mapea la mutacion de solicitud de restablecimiento de contraseña
  @MutationMapping
  public Boolean requestPasswordReset(@Argument String email) {
    requestPasswordResetUseCase.requestReset(
        email); // Ejecucion del caso de uso de solicitud de restablecimiento de contraseña
    return true; // Retorna true si la solicitud de restablecimiento de contraseña fue exitosa
  }

  // Mapea la mutacion de restablecimiento de contraseña
  @MutationMapping
  public Boolean resetPassword(@Argument ResetPasswordInput input) {
    ResetPasswordUseCase.ResetCommand command =
        new ResetPasswordUseCase.ResetCommand(input.token, input.newPassword);
    resetPasswordUseCase.reset(
        command); // Ejecucion del caso de uso de restablecimiento de contraseña
    return true; // Retorna true si el restablecimiento de contraseña fue exitoso
  }

  // DTO de entrada para la mutacion de registro de usuarios
  public record RegisterUserInput(String email, String password) {}

  // DTO de entrada para la mutacion de inicio de sesión de usuarios
  public record LoginInput(String email, String password) {}

  // DTO de entrada para la mutacion de restablecimiento de contraseña
  public record ResetPasswordInput(String token, String newPassword) {}
}
