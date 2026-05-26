package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service; // Importante

@Service
public class UpdatePlaceService implements UpdatePlaceUseCase {

  private final PlaceRepository placeRepository;
  private final GeometryFactory geometryFactory;

  public UpdatePlaceService(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
    this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
  }

  @Override
  public Place update(UpdateCommand command) {

    Place place =
        placeRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Lugar no encontrado"));

    Point location = place.getLocation();
    if (command.latitude() != null && command.longitude() != null) {
      location =
          geometryFactory.createPoint(new Coordinate(command.longitude(), command.latitude()));
    }

    Place updatePlace =
        Place.builder()
            .id(command.id())
            .name(command.name() != null ? command.name() : place.getName())
            .description(
                command.description() != null ? command.description() : place.getDescription())
            .category(command.category() != null ? command.category() : place.getCategory())
            .priceRange(command.priceRange() != null ? command.priceRange() : place.getPriceRange())
            .address(command.address() != null ? command.address() : place.getAddress())
            .location(location)
            .active(command.active() != null ? command.active() : place.isActive())
            .createdAt(place.getCreatedAt())
            .build();

    return placeRepository.save(updatePlace);
  }
}
