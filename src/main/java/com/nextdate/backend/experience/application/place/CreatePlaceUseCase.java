package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;

public interface CreatePlaceUseCase {

  Place create(CreateCommand command);

  record CreateCommand(
      String name,
      String description,
      PlaceCategory category,
      PriceRange priceRange,
      String address,
      double latitude,
      double longitude) {}
}
