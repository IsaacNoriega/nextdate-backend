package com.nextdate.backend.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlaceRepository {
  Place save(Place place);

  Optional<Place> findById(UUID id);

  List<Place> findNearby(
      double latitude, double longitude, double radiusInKm, PlaceCategory category);
}
