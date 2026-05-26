package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetNearbyPlacesService implements GetNearbyPlacesUseCase {

  private final PlaceRepository placeRepository;

  public GetNearbyPlacesService(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
  }

  @Override
  public List<Place> getNearby(
      double latitude, double longitude, double radiusInKm, PlaceCategory category) {
    return placeRepository.findNearby(latitude, longitude, radiusInKm, category);
  }
}
