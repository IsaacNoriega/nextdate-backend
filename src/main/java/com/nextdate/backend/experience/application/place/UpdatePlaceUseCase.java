package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import java.util.UUID;

public interface UpdatePlaceUseCase {
  Place update(UpdateCommand command);

  record UpdateCommand(
      UUID id,
      String name,
      String description,
      PlaceCategory category,
      PriceRange priceRange,
      String address,
      Double latitude,
      Double longitude,
      Boolean active) {}
}
