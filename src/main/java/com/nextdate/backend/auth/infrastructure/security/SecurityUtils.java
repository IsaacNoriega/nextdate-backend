package com.nextdate.backend.auth.infrastructure.security;

import graphql.GraphQLContext;
import java.util.Optional;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

  private SecurityUtils() {}

  public static Optional<UUID> getAuthenticatedUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof UUID userId) {
      return Optional.of(userId);
    }
    return Optional.empty();
  }

  public static Optional<UUID> getAuthenticatedUserId(GraphQLContext context) {
    if (context != null && context.hasKey(JwtGraphQlInterceptor.USER_ID_CONTEXT_KEY)) {
      Object idObj = context.get(JwtGraphQlInterceptor.USER_ID_CONTEXT_KEY);
      if (idObj instanceof UUID uuid) {
        return Optional.of(uuid);
      }
    }
    return getAuthenticatedUserId();
  }

  public static UUID requireAuthenticatedUserId(GraphQLContext context) {
    return getAuthenticatedUserId(context)
        .orElseThrow(
            () -> new AccessDeniedException("Acceso no autorizado: Se requiere token JWT"));
  }

  public static void validateUserOwnership(UUID requestedUserId, GraphQLContext context) {
    UUID currentUserId = requireAuthenticatedUserId(context);
    if (!currentUserId.equals(requestedUserId)) {
      throw new AccessDeniedException(
          "Acceso denegado: No tienes permisos para operar con otro usuario");
    }
  }
}
