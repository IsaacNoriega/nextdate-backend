package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.Profile;
import java.time.LocalDate;
import java.util.UUID;

public interface CreateProfileUseCase {

  Profile create(CreateCommand command);

  record CreateCommand(
      UUID userId,
      String username,
      LocalDate birthdate,
      Gender gender,
      String bio,
      double latitude,
      double longitude) {}
}
