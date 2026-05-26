package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.place.CreatePlaceUseCase;
import com.nextdate.backend.experience.application.place.CreatePlaceUseCase.CreateCommand;
import com.nextdate.backend.experience.application.place.GetNearbyPlacesUseCase;
import com.nextdate.backend.experience.application.place.UpdatePlaceUseCase;
import com.nextdate.backend.experience.application.place.UpdatePlaceUseCase.UpdateCommand;
import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.PriceRange;
import java.util.List;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class PlaceGraphQLController {

  private final GetNearbyPlacesUseCase getNearbyPlacesUseCase;
  private final PlaceRepository placeRepository;
  private final CreatePlaceUseCase createPlaceUseCase;
  private final UpdatePlaceUseCase updatePlaceUseCase;

  public PlaceGraphQLController(
      GetNearbyPlacesUseCase getNearbyPlacesUseCase,
      PlaceRepository placeRepository,
      CreatePlaceUseCase createPlaceUseCase,
      UpdatePlaceUseCase updatePlaceUseCase) {
    this.getNearbyPlacesUseCase = getNearbyPlacesUseCase;
    this.placeRepository = placeRepository;
    this.createPlaceUseCase = createPlaceUseCase;
    this.updatePlaceUseCase = updatePlaceUseCase;
  }

  // get place by id
  @QueryMapping
  public Place placeById(@Argument UUID id) {
    return placeRepository.findById(id).orElse(null);
  }

  // get nearby places
  @QueryMapping
  public List<Place> nearbyPlaces(
      @Argument double latitude,
      @Argument double longitude,
      @Argument double radiusInKm,
      @Argument PlaceCategory category) {
    return getNearbyPlacesUseCase.getNearby(latitude, longitude, radiusInKm, category);
  }

  // create a new place
  @MutationMapping
  public Place createPlace(@Argument CreatePlaceInput input) {
    CreateCommand command =
        new CreateCommand(
            input.name(),
            input.description(),
            input.category(),
            input.priceRange(),
            input.address(),
            input.latitude(),
            input.longitude());

    return createPlaceUseCase.create(command);
  }

  // update an existing place
  @MutationMapping
  public Place updatePlace(@Argument UpdatePlaceInput input) {
    UpdateCommand command =
        new UpdateCommand(
            input.id(),
            input.name(),
            input.description(),
            input.category(),
            input.priceRange(),
            input.address(),
            input.latitude(),
            input.longitude(),
            input.active());

    return updatePlaceUseCase.update(command);
  }

  public record CreatePlaceInput(
      String name,
      String description,
      PlaceCategory category,
      PriceRange priceRange,
      String address,
      double latitude,
      double longitude) {}

  public record UpdatePlaceInput(
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
