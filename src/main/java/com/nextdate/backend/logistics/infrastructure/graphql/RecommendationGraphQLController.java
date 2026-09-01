package com.nextdate.backend.logistics.infrastructure.graphql;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.logistics.application.RecommendItineraryUseCase;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RecommendationGraphQLController {
  private final RecommendItineraryUseCase recommendItineraryUseCase;

  public RecommendationGraphQLController(RecommendItineraryUseCase recommendItineraryUseCase) {
    this.recommendItineraryUseCase = recommendItineraryUseCase;
  }

  @MutationMapping
  public Itinerary recommendItinerary(
      @Argument RecommendItineraryInput input, graphql.GraphQLContext context) {
    com.nextdate.backend.auth.infrastructure.security.SecurityUtils.validateUserOwnership(
        input.userId(), context);
    return recommendItineraryUseCase.recommend(input.userId(), input.prompt());
  }

  public record RecommendItineraryInput(UUID userId, String prompt) {}
}
