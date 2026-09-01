package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.shared.GetSharedExperiencesUseCase;
import com.nextdate.backend.experience.application.shared.ShareExperienceUseCase;
import com.nextdate.backend.experience.application.shared.ShareExperienceUseCase.ShareCommand;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.BatchMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
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
  public SharedExperience shareExperience(
      @Argument ShareExperienceInput input, graphql.GraphQLContext context) {
    com.nextdate.backend.auth.infrastructure.security.SecurityUtils.validateUserOwnership(
        input.userId(), context);

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

  // BatchMapping para resolver los itinerarios de todas las experiencias compartidas de forma
  // conjunta
  @BatchMapping(typeName = "SharedExperience", field = "itinerary")
  public Map<SharedExperience, Itinerary> itinerary(List<SharedExperience> experiences) {
    Set<UUID> itineraryIds =
        experiences.stream()
            .map(SharedExperience::getItineraryId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    Map<UUID, Itinerary> itineraryMap =
        itineraryRepository.findAllById(itineraryIds).stream()
            .collect(Collectors.toMap(Itinerary::getId, itin -> itin));
    return experiences.stream()
        .collect(Collectors.toMap(exp -> exp, exp -> itineraryMap.get(exp.getItineraryId())));
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
