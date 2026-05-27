package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.profile.CreateProfileUseCase;
import com.nextdate.backend.experience.application.profile.CreateProfileUseCase.CreateCommand;
import com.nextdate.backend.experience.application.profile.GetNearbyProfilesUseCase;
import com.nextdate.backend.experience.application.profile.UpdateProfileUseCase;
import com.nextdate.backend.experience.application.profile.UpdateProfileUseCase.UpdateCommand;
import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
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

    DietaryPreference dietary =
        input.dietaryPreference() != null
            ? DietaryPreference.valueOf(input.dietaryPreference().toUpperCase())
            : DietaryPreference.NONE;

    PriceRange price =
        input.preferredPriceRange() != null
            ? PriceRange.valueOf(input.preferredPriceRange().toUpperCase())
            : PriceRange.MODERATE;

    Set<PlaceCategory> interests =
        input.interests() != null
            ? input.interests().stream()
                .map(c -> PlaceCategory.valueOf(c.toUpperCase()))
                .collect(Collectors.toSet())
            : Set.of();

    CreateCommand command =
        new CreateCommand(
            input.userId(),
            input.username(),
            LocalDate.parse(input.birthdate()),
            Gender.valueOf(input.gender().toUpperCase()),
            input.bio(),
            input.latitude(),
            input.longitude(),
            dietary,
            price,
            interests);
    return createProfileUseCase.create(command);
  }

  // UPDATE PROFILE
  @MutationMapping
  public Profile updateProfile(@Argument UpdateProfileInput input) {

    DietaryPreference dietary =
        input.dietaryPreference() != null
            ? DietaryPreference.valueOf(input.dietaryPreference().toUpperCase())
            : DietaryPreference.NONE;

    PriceRange price =
        input.preferredPriceRange() != null
            ? PriceRange.valueOf(input.preferredPriceRange().toUpperCase())
            : PriceRange.MODERATE;

    Set<PlaceCategory> interests =
        input.interests() != null
            ? input.interests().stream()
                .map(c -> PlaceCategory.valueOf(c.toUpperCase()))
                .collect(Collectors.toSet())
            : Set.of();

    UpdateCommand command =
        new UpdateCommand(
            input.id(),
            input.userId(),
            input.username(),
            LocalDate.parse(input.birthdate()),
            Gender.valueOf(input.gender().toUpperCase()),
            input.bio(),
            input.latitude(),
            input.longitude(),
            dietary,
            price,
            interests);
    return updateProfileUseCase.update(command);
  }

  public record CreateProfileInput(
      UUID userId,
      String username,
      String birthdate,
      String gender,
      String bio,
      double latitude,
      double longitude,
      String dietaryPreference,
      String preferredPriceRange,
      List<String> interests) {}

  public record UpdateProfileInput(
      UUID id,
      UUID userId,
      String username,
      String birthdate,
      String gender,
      String bio,
      double latitude,
      double longitude,
      String dietaryPreference,
      String preferredPriceRange,
      List<String> interests) {}
}
