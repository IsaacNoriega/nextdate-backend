package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.time.LocalDate;
import java.util.Set;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

@Service
public class UpdateProfileService implements UpdateProfileUseCase {

  private final ProfileRepository profileRepository;
  private final GeometryFactory geometryFactory;

  public UpdateProfileService(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
    this.geometryFactory = new GeometryFactory(new PrecisionModel(), 4326); // WGS 84
  }

  @Override
  public Profile update(UpdateCommand command) {

    // Verificar que el perfil exista
    Profile profile =
        profileRepository
            .findById(command.id())
            .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));

    // Verificar que el usuario sea el dueño del perfil
    if (!profile.getUserId().equals(command.userId())) {
      throw new IllegalArgumentException("No tienes permiso para actualizar este perfil");
    }
    // Mapear campos con fallback a los valores existentes si vienen nulos (actualización parcial)
    String username = command.username() != null ? command.username() : profile.getUsername();
    LocalDate birthdate =
        command.birthdate() != null ? command.birthdate() : profile.getBirthdate();
    Gender gender = command.gender() != null ? command.gender() : profile.getGender();
    String bio = command.bio() != null ? command.bio() : profile.getBio();
    String avatarUrl =
        command.avatarUrl() != null ? command.avatarUrl() : profile.getAvatarUrl();

    Point location =
        (command.longitude() != null && command.latitude() != null)
            ? geometryFactory.createPoint(new Coordinate(command.longitude(), command.latitude()))
            : profile.getLocation();

    DietaryPreference dietary =
        command.dietaryPreference() != null
            ? command.dietaryPreference()
            : profile.getDietaryPreference();

    PriceRange price =
        command.preferredPriceRange() != null
            ? command.preferredPriceRange()
            : profile.getPreferredPriceRange();

    Set<PlaceCategory> interests =
        command.interests() != null ? command.interests() : profile.getInterests();

    // Crear el perfil actualizado
    Profile updatedProfile =
        Profile.builder()
            .id(profile.getId())
            .userId(profile.getUserId())
            .username(username)
            .birthdate(birthdate)
            .gender(gender)
            .bio(bio)
            .avatarUrl(avatarUrl)
            .location(location)
            .dietaryPreference(dietary)
            .preferredPriceRange(price)
            .interests(interests)
            .active(profile.getActive())
            .build();

    return profileRepository.save(updatedProfile);
  }
}
