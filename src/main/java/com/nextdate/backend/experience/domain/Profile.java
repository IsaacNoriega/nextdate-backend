package com.nextdate.backend.experience.domain;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@Builder
@AllArgsConstructor
public class Profile { // DTO para manejo de datos
  private final UUID id;
  private final UUID userId;
  private final String username;
  private final LocalDate birthdate;
  private final Gender gender;
  private final String bio;
  private final Point location;
  private final Boolean active;

  private final DietaryPreference dietaryPreference;
  private final PriceRange preferredPriceRange;
  private final Set<PlaceCategory> interests;

  public double getLatitude() {
    return location != null ? location.getY() : 0.0;
  }

  public double getLongitude() {
    return location != null ? location.getX() : 0.0;
  }

  public Profile deactivate() {
    return Profile.builder()
        .id(this.id)
        .userId(this.userId)
        .username(this.username)
        .birthdate(this.birthdate)
        .gender(this.gender)
        .bio(this.bio)
        .location(this.location)
        .active(false)
        .dietaryPreference(this.dietaryPreference)
        .preferredPriceRange(this.preferredPriceRange)
        .interests(this.interests)
        .build();
  }
}
