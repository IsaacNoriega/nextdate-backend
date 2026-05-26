package com.nextdate.backend.experience.application.place;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class CreatePlaceService implements CreatePlaceUseCase {

  private final PlaceRepository placeRepository;
  private final GeometryFactory geometryFactory;

  public CreatePlaceService(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
    this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
  }

  public Place create(CreateCommand command) {

    Point location =
        geometryFactory.createPoint(new Coordinate(command.longitude(), command.latitude()));

    Place place =
        Place.builder()
            .id(UUID.randomUUID())
            .name(command.name())
            .description(command.description())
            .category(command.category())
            .priceRange(command.priceRange())
            .address(command.address())
            .location(location)
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

    return placeRepository.save(place);
  }
}
