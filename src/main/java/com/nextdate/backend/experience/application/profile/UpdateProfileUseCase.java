package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import java.time.LocalDate;
import java.util.Set;
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
      String avatarUrl,
      Double latitude,
      Double longitude,
      DietaryPreference dietaryPreference,
      PriceRange preferredPriceRange,
      Set<PlaceCategory> interests) {}
}
