package com.nextdate.backend.auth.application.login;

import com.nextdate.backend.auth.domain.User;

public interface LoginUserUseCase {

  LoginResult login(LoginCommand command); // Command con los datos del usuario

  record LoginCommand(String email, String password) {} // Command con los datos del usuario

  record LoginResult(User user, String token) {} // Result con los datos del usuario y el token
}
