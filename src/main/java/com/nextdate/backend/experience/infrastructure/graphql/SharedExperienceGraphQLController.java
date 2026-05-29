package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.shared.GetSharedExperiencesUseCase;
import com.nextdate.backend.experience.application.shared.ShareExperienceUseCase;
import com.nextdate.backend.experience.application.shared.ShareExperienceUseCase.ShareCommand;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class SharedExperienceGraphQLController {

  private final ShareExperienceUseCase shareExperienceUseCase;
  private final GetSharedExperiencesUseCase getSharedExperiencesUseCase;
  private final ItineraryRepository itineraryRepository;

  public SharedExperienceGraphQLController(
      ShareExperienceUseCase shareExperienceUseCase,
      GetSharedExperiencesUseCase getSharedExperiencesUseCase,
      ItineraryRepository itineraryRepository) {
    this.shareExperienceUseCase = shareExperienceUseCase;
    this.getSharedExperiencesUseCase = getSharedExperiencesUseCase;
    this.itineraryRepository = itineraryRepository;
  }

  @QueryMapping
  public List<SharedExperience> sharedExperiences() {
    return getSharedExperiencesUseCase.getActive();
  }

  @QueryMapping
  public List<SharedExperience> sharedExperiencesByUserId(@Argument UUID userId) {
    return getSharedExperiencesUseCase.getByUserId(userId);
  }

  @MutationMapping
  public SharedExperience shareExperience(@Argument ShareExperienceInput input) {
    ShareCommand command =
        new ShareCommand(
            input.userId(),
            input.itineraryId(),
            input.title(),
            input.description(),
            input.tips(),
            input.actualCost(),
            input.rating(),
            input.imageUrls());
    return shareExperienceUseCase.share(command);
  }

  @SchemaMapping(typeName = "SharedExperience", field = "itinerary")
  public Itinerary itinerary(SharedExperience experience) {
    return itineraryRepository.findById(experience.getItineraryId()).orElse(null);
  }

  public record ShareExperienceInput(
      UUID userId,
      UUID itineraryId,
      String title,
      String description,
      String tips,
      double actualCost,
      int rating,
      List<String> imageUrls) {}
}
