package com.nextdate.backend;

import graphql.GraphQLError;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalGraphQLControllerAdvice {

  @GraphQlExceptionHandler
  public GraphQLError handleIllegalArgumentException(IllegalArgumentException ex) {
    return GraphQLError.newError()
        .message(ex.getMessage())
        .errorType(ErrorType.BAD_REQUEST)
        .build();
  }

  @GraphQlExceptionHandler
  public GraphQLError handleRuntimeException(RuntimeException ex) {
    return GraphQLError.newError()
        .message(ex.getMessage())
        .errorType(ErrorType.INTERNAL_ERROR)
        .build();
  }
}
