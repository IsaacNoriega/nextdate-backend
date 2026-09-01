package com.nextdate.backend.auth.infrastructure.security;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.springframework.graphql.server.WebGraphQlInterceptor;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

@Component
public class JwtGraphQlInterceptor implements WebGraphQlInterceptor {

  public static final String USER_ID_CONTEXT_KEY = "authenticatedUserId";
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenService jwtTokenService;

  public JwtGraphQlInterceptor(JwtTokenService jwtTokenService) {
    this.jwtTokenService = jwtTokenService;
  }

  @Override
  public Mono<WebGraphQlResponse> intercept(WebGraphQlRequest request, Chain chain) {
    String authHeader = request.getHeaders().getFirst(AUTHORIZATION_HEADER);

    if (StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)) {
      String token = authHeader.substring(BEARER_PREFIX.length()).trim();
      Optional<UUID> userIdOpt = jwtTokenService.extractUserId(token);
      Optional<String> emailOpt = jwtTokenService.extractEmail(token);

      if (userIdOpt.isPresent() && emailOpt.isPresent()) {
        UUID userId = userIdOpt.get();
        String email = emailOpt.get();

        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userId, email, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        request.configureExecutionInput(
            (executionInput, builder) ->
                builder
                    .graphQLContext(
                        graphQLContext -> graphQLContext.put(USER_ID_CONTEXT_KEY, userId))
                    .build());
      }
    }

    return chain.next(request);
  }
}
