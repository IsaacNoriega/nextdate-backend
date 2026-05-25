package com.nextdate.backend.auth.application.recovery;

public interface ResetPasswordUseCase {

  void reset(ResetCommand command);

  record ResetCommand(String token, String newPassword) {}
  ;
}
