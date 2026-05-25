package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.profile.CreateProfileUseCase;
import com.nextdate.backend.experience.application.profile.CreateProfileUseCase.CreateCommand;
import com.nextdate.backend.experience.application.profile.GetNearbyProfilesUseCase;
import com.nextdate.backend.experience.application.profile.UpdateProfileUseCase;
import com.nextdate.backend.experience.application.profile.UpdateProfileUseCase.UpdateCommand;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ProfileGraphQLController {

  private final ProfileRepository profileRepository;
  private final CreateProfileUseCase createProfileUseCase;
  private final UpdateProfileUseCase updateProfileUseCase;
  private final GetNearbyProfilesUseCase getNearbyProfilesUseCase;

  public ProfileGraphQLController(
      ProfileRepository profileRepository,
      CreateProfileUseCase createProfileUseCase,
      UpdateProfileUseCase updateProfileUseCase,
      GetNearbyProfilesUseCase getNearbyProfilesUseCase) {
    this.profileRepository = profileRepository;
    this.createProfileUseCase = createProfileUseCase;
    this.updateProfileUseCase = updateProfileUseCase;
    this.getNearbyProfilesUseCase = getNearbyProfilesUseCase;
  }

  // GET PERFIL POR USER ID
  @QueryMapping
  public Profile profileByUserId(@Argument UUID userId) {
    return profileRepository.findByUserId(userId).orElse(null);
  }

  // GET PERFILES CERCANOS
  @QueryMapping
  public List<Profile> nearbyProfiles(
      @Argument double longitude, @Argument double latitude, @Argument double radiusInKm) {
    return getNearbyProfilesUseCase.getNearby(longitude, latitude, radiusInKm);
  }

  // CREATE PROFILE
  @MutationMapping
  public Profile createProfile(@Argument CreateProfileInput input) {
    CreateCommand command =
        new CreateCommand(
            input.userId(),
            input.username(),
            LocalDate.parse(input.birthdate()),
            Gender.valueOf(input.gender().toUpperCase()),
            input.bio(),
            input.latitude(),
            input.longitude());
    return createProfileUseCase.create(command);
  }

  // UPDATE PROFILE
  @MutationMapping
  public Profile updateProfile(@Argument UpdateProfileInput input) {
    UpdateCommand command =
        new UpdateCommand(
            input.id(),
            input.userId(),
            input.username(),
            LocalDate.parse(input.birthdate()),
            Gender.valueOf(input.gender().toUpperCase()),
            input.bio(),
            input.latitude(),
            input.longitude());
    return updateProfileUseCase.update(command);
  }

  public record CreateProfileInput(
      UUID userId,
      String username,
      String birthdate,
      String gender,
      String bio,
      double latitude,
      double longitude) {}

  public record UpdateProfileInput(
      UUID id,
      UUID userId,
      String username,
      String birthdate,
      String gender,
      String bio,
      double latitude,
      double longitude) {}
}
