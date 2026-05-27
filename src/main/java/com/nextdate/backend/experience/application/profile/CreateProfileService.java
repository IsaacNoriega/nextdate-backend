package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.util.Set;
import java.util.UUID;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class CreateProfileService implements CreateProfileUseCase {

  private final ProfileRepository profileRepository;
  private final GeometryFactory geometryFactory;

  public CreateProfileService(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
    this.geometryFactory =
        new GeometryFactory(
            new PrecisionModel(),
            4326); // SRID 4326 es el estándar de WGS 84 (el estándar de facto para GPS)
  }

  @Override
  public Profile create(CreateCommand command) {

    // Verificar si el usuario ya tiene un perfil
    if (profileRepository.findByUserId(command.userId()).isPresent()) {
      throw new IllegalArgumentException("El usuario ya tiene un perfil creado");
    }

    // Creación del punto de ubicación
    Point location =
        geometryFactory.createPoint(new Coordinate(command.longitude(), command.latitude()));

    DietaryPreference dietary =
        command.dietaryPreference() != null ? command.dietaryPreference() : DietaryPreference.NONE;
    PriceRange price =
        command.preferredPriceRange() != null ? command.preferredPriceRange() : PriceRange.MODERATE;
    Set<PlaceCategory> interests = command.interests() != null ? command.interests() : Set.of();

    // Creación del perfil
    Profile profile =
        Profile.builder()
            .id(UUID.randomUUID())
            .userId(command.userId())
            .username(command.username())
            .birthdate(command.birthdate())
            .gender(command.gender())
            .bio(command.bio())
            .location(location)
            .active(true)
            .dietaryPreference(dietary)
            .preferredPriceRange(price)
            .interests(interests)
            .build();

    // Guardar el perfil
    return profileRepository.save(profile);
  }
}
