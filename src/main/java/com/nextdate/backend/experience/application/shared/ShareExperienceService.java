package com.nextdate.backend.experience.application.shared;

import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.experience.domain.SharedExperienceRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ShareExperienceService implements ShareExperienceUseCase {

  private final SharedExperienceRepository sharedExperienceRepository;
  private final ItineraryRepository itineraryRepository;

  public ShareExperienceService(
      SharedExperienceRepository sharedExperienceRepository,
      ItineraryRepository itineraryRepository) {
    this.sharedExperienceRepository = sharedExperienceRepository;
    this.itineraryRepository = itineraryRepository;
  }

  @Override
  public SharedExperience share(ShareCommand command) {
    // 1. Validar que el itinerario exista si se proporcionó uno
    if (command.itineraryId() != null) {
      itineraryRepository
          .findById(command.itineraryId())
          .orElseThrow(
              () ->
                  new IllegalArgumentException(
                      "Itinerario no encontrado con ID: " + command.itineraryId()));
    }

    // 2. Construir la experiencia compartida
    SharedExperience sharedExperience =
        SharedExperience.builder()
            .id(UUID.randomUUID())
            .userId(command.userId())
            .itineraryId(command.itineraryId())
            .title(command.title())
            .description(command.description())
            .tips(command.tips())
            .actualCost(command.actualCost())
            .rating(command.rating())
            .active(true)
            .createdAt(LocalDateTime.now())
            .imageUrls(command.imageUrls())
            .build();

    // 3. Guardar en el repositorio
    return sharedExperienceRepository.save(sharedExperience);
  }
}
