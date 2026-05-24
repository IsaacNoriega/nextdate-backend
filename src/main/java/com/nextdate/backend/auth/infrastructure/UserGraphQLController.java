package com.nextdate.backend.auth.infrastructure;

import com.nextdate.backend.auth.application.LoginUserUseCase;
import com.nextdate.backend.auth.application.LoginUserUseCase.LoginCommand;
import com.nextdate.backend.auth.application.RegisterUserUseCase;
import com.nextdate.backend.auth.application.RegisterUserUseCase.RegisterUserCommand;
import com.nextdate.backend.auth.domain.User;
import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

@Controller
public class UserGraphQLController {

  // Instancia del caso de uso de registro de usuarios
  private final RegisterUserUseCase registerUserUseCase;
  // Instancia del caso de uso de inicio de sesión de usuarios
  private final LoginUserUseCase loginUserUseCase;

  // Constructor para la inyeccion de dependencias
  public UserGraphQLController(
      RegisterUserUseCase registerUserUseCase, LoginUserUseCase loginUserUseCase) {
    this.registerUserUseCase = registerUserUseCase;
    this.loginUserUseCase = loginUserUseCase;
  }

  // Mapea la mutacion de registro de usuarios
  @MutationMapping
  public User registerUser(@Argument RegisterUserInput input) {
    // Creacion del comando de registro de usuarios
    RegisterUserCommand command = new RegisterUserCommand(input.email, input.password);
    // Ejecucion del caso de uso de registro de usuarios
    return registerUserUseCase.register(command);
  }

  // Mapea la mutacion de inicio de sesión de usuarios
  @MutationMapping
  public LoginUserUseCase.LoginResult login(@Argument LoginInput input) {

    // Creacion del comando de inicio de sesión de usuarios
    LoginCommand command = new LoginCommand(input.email, input.password);

    // Ejecucion del caso de uso de inicio de sesión de usuarios
    return loginUserUseCase.login(command);
  }

  // DTO de entrada para la mutacion de registro de usuarios
  public record RegisterUserInput(String email, String password) {}

  // DTO de entrada para la mutacion de inicio de sesión de usuarios
  public record LoginInput(String email, String password) {}

  @GraphQlExceptionHandler
  public GraphQLError handleIllegalArgumentException(IllegalArgumentException ex) {

    return GraphQLError.newError()
        .message(ex.getMessage())
        .errorType(ErrorType.BAD_REQUEST)
        .build();
  }
}
