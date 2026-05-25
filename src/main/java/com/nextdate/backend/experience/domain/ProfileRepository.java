package com.nextdate.backend.experience.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {

  Optional<Profile> findById(UUID id);

  Optional<Profile> findByUserId(UUID userId);

  Profile save(Profile profile);

  List<Profile> findNearby(
      double longitude, double latitude, double radiusInKm); // Lista de perfiles cercanos
}
