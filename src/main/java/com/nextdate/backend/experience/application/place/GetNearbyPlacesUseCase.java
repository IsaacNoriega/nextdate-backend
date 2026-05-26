package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import java.util.List;

public interface GetNearbyPlacesUseCase {
  List<Place> getNearby(
      double latitude, double longitude, double radiusInKm, PlaceCategory category);
}
