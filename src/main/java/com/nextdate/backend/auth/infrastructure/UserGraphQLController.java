package com.nextdate.backend.auth.infrastructure;

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

  // Constructor para la inyeccion de dependencias
  public UserGraphQLController(RegisterUserUseCase registerUserUseCase) {
    this.registerUserUseCase = registerUserUseCase;
  }

  // Mapea la mutacion de registro de usuarios
  @MutationMapping
  public User registerUser(@Argument RegisterUserInput input) {
    // Creacion del comando de registro de usuarios
    RegisterUserCommand command = new RegisterUserCommand(input.email, input.password);
    // Ejecucion del caso de uso de registro de usuarios
    return registerUserUseCase.register(command);
  }

  // DTO de entrada para la mutacion de registro de usuarios
  public record RegisterUserInput(String email, String password) {}

  @GraphQlExceptionHandler
  public GraphQLError handleIllegalArgumentException(IllegalArgumentException ex) {

    return GraphQLError.newError()
        .message(ex.getMessage())
        .errorType(ErrorType.BAD_REQUEST)
        .build();
  }
}
