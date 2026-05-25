package com.nextdate.backend.auth.application.login;

import com.nextdate.backend.auth.domain.User;

public interface TokenService {

  String generateToken(User user); // Genera el token
}
