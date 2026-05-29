package com.nextdate.backend.experience.application.shared;

import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;
import java.util.UUID;

public interface GetSharedExperiencesUseCase {

  List<SharedExperience> getActive();

  List<SharedExperience> getByUserId(UUID userId);
}
