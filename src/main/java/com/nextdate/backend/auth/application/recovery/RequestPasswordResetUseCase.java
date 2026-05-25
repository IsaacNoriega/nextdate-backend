package com.nextdate.backend.auth.application.recovery;

public interface RequestPasswordResetUseCase {
  void requestReset(String email);
}
