package com.nextdate.backend.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedExperienceRepository {

  SharedExperience save(SharedExperience experience);

  Optional<SharedExperience> findById(UUID id);

  List<SharedExperience> findAllActive();

  List<SharedExperience> findByUserId(UUID userId);
}
