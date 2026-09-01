package com.nextdate.backend.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ItineraryRepository {

  Itinerary save(Itinerary itinerary);

  Optional<Itinerary> findById(UUID id);

  List<Itinerary> findAllById(Iterable<UUID> ids);

  List<Itinerary> findByUserId(UUID userId);
}
