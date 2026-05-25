package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.Profile;
import java.time.LocalDate;
import java.util.UUID;

public interface UpdateProfileUseCase {

  Profile update(UpdateCommand command);

  record UpdateCommand(
      UUID id,
      UUID userId,
      String username,
      LocalDate birthdate,
      Gender gender,
      String bio,
      double latitude,
      double longitude) {}
}
