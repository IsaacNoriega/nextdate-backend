package com.nextdate.backend.auth.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.nextdate.backend.auth.application.login.TokenService;
import com.nextdate.backend.auth.domain.User;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService implements TokenService {

  private final String secretKey;
  private final long expirationTime;
  private final Algorithm algorithm;
  private final JWTVerifier verifier;

  public JwtTokenService(
      @Value(
              "${jwt.secret:nextdate_super_secret_key_development_only_minimum_64_characters_long_for_security_compliance}")
          String secretKey,
      @Value("${jwt.expiration-ms:864000000}") long expirationTime) {
    this.secretKey = secretKey;
    this.expirationTime = expirationTime;
    this.algorithm = Algorithm.HMAC512(secretKey);
    this.verifier = JWT.require(this.algorithm).build();
  }

  @Override
  public String generateToken(User user) {
    return JWT.create()
        .withSubject(user.getId().toString())
        .withClaim("email", user.getEmail())
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
        .sign(algorithm);
  }

  public Optional<DecodedJWT> verifyToken(String token) {
    try {
      return Optional.of(verifier.verify(token));
    } catch (JWTVerificationException e) {
      return Optional.empty();
    }
  }

  public Optional<UUID> extractUserId(String token) {
    return verifyToken(token)
        .map(DecodedJWT::getSubject)
        .map(
            sub -> {
              try {
                return UUID.fromString(sub);
              } catch (IllegalArgumentException e) {
                return null;
              }
            });
  }

  public Optional<String> extractEmail(String token) {
    return verifyToken(token).map(jwt -> jwt.getClaim("email").asString());
  }
}
