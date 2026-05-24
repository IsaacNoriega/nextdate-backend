package com.nextdate.backend.auth.infrastructure;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.nextdate.backend.auth.application.TokenService;
import com.nextdate.backend.auth.domain.User;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService implements TokenService {

  private static final String SECRET_KEY =
      "nextdate_super_secret_key_development_only"; // TODO: CAMBIAR EN PRODUCCION
  private static final long EXPIRATION_TIME = 864_000_000; // 10 días en milisegundos

  @Override
  public String generateToken(User user) {
    return JWT.create()
        .withSubject(user.getId().toString()) // Subject es el ID del usuario
        .withClaim("email", user.getEmail()) // Claim es el email del usuario
        .withIssuedAt(new Date()) // Fecha de emisión
        .withExpiresAt(
            new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Fecha de expiración
        .sign(Algorithm.HMAC512(SECRET_KEY)); // Firma con el algoritmo HMAC512 y la llave secreta
  }
}
