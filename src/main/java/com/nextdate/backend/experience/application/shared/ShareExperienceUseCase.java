package com.nextdate.backend.experience.application.shared;

import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;
import java.util.UUID;

public interface ShareExperienceUseCase {

  SharedExperience share(ShareCommand command);

  record ShareCommand(
      UUID userId,
      UUID itineraryId,
      String title,
      String description,
      String tips,
      double actualCost,
      int rating,
      List<String> imageUrls) {}
}
