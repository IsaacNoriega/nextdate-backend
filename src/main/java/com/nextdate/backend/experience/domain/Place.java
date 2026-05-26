package com.nextdate.backend.experience.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.locationtech.jts.geom.Point;

@Getter
@Builder
@AllArgsConstructor
public class Place {

  private final UUID id;
  private final String name;
  private final String description;
  private final PlaceCategory category;
  private final PriceRange priceRange;
  private final String address;
  private final Point location;
  private final boolean active;
  private final LocalDateTime createdAt;

  public double getLatitude() {
    return location != null ? location.getY() : 0.0;
  }

  public double getLongitude() {
    return location != null ? location.getX() : 0.0;
  }
}
