package com.nextdate.backend.experience.application.shared;

import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.experience.domain.SharedExperienceRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetSharedExperiencesService implements GetSharedExperiencesUseCase {

  private final SharedExperienceRepository repository;

  public GetSharedExperiencesService(SharedExperienceRepository repository) {
    this.repository = repository;
  }

  @Override
  public List<SharedExperience> getActive() {
    return repository.findAllActive();
  }

  @Override
  public List<SharedExperience> getByUserId(UUID userId) {
    return repository.findByUserId(userId);
  }
}
