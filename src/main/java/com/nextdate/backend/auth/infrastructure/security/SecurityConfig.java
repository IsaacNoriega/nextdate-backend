package com.nextdate.backend.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  // Definimos el bean de encoding de contraseñas con BCrypt
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  // Definimos las reglas de acceso a las rutas
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http
        // Deshabilitamos CSRF porque no usaremos sesiones
        .csrf(AbstractHttpConfigurer::disable)
        // Autenticación HTTP
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(
                        "/graphql", "/graphiql") // Rutas que no requieren autenticación
                    .permitAll() // Permite el acceso a estas rutas
                    .anyRequest()
                    .authenticated() // Todas las demás rutas requieren autenticación
            );

    return http.build();
  }
}
