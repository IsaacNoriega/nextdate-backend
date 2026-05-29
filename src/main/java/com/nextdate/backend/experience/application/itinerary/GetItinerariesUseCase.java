package com.nextdate.backend.experience.application.itinerary;

import com.nextdate.backend.experience.domain.Itinerary;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GetItinerariesUseCase {
  List<Itinerary> getByUserId(UUID userId);

  Optional<Itinerary> getById(UUID id);
}
