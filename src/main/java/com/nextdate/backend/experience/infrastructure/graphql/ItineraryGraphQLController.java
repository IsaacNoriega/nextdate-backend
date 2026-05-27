package com.nextdate.backend.experience.infrastructure.graphql;

import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase;
import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase.CreateCommand;
import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase.CreateItemCommand;
import com.nextdate.backend.experience.application.itinerary.GetItinerariesUseCase;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryItem;
import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.TransportType;
import java.util.List;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ItineraryGraphQLController {

  private final GetItinerariesUseCase getItinerariesUseCase;
  private final CreateItineraryUseCase createItineraryUseCase;
  private final PlaceRepository placeRepository;

  public ItineraryGraphQLController(
      GetItinerariesUseCase getItinerariesUseCase,
      CreateItineraryUseCase createItineraryUseCase,
      PlaceRepository placeRepository) {
    this.getItinerariesUseCase = getItinerariesUseCase;
    this.createItineraryUseCase = createItineraryUseCase;
    this.placeRepository = placeRepository;
  }

  // Itineraries por ID de usuario
  @QueryMapping
  public List<Itinerary> itinerariesByUserId(@Argument UUID userId) {
    return getItinerariesUseCase.getByUserId(userId);
  }

  // Itinerario por ID
  @QueryMapping
  public Itinerary itineraryById(@Argument UUID id) {
    return getItinerariesUseCase.getById(id).orElse(null);
  }

  @MutationMapping
  public Itinerary createItinerary(@Argument CreateItineraryInput input) {
    List<CreateItemCommand> itemCommands =
        input.items().stream()
            .map(
                item ->
                    new CreateItemCommand(
                        item.placeId(),
                        item.sequenceOrder(),
                        item.durationInMinutes() != null ? item.durationInMinutes() : 60,
                        item.notes(),
                        item.transportToNext() != null
                            ? TransportType.valueOf(item.transportToNext().toUpperCase())
                            : TransportType.NONE,
                        item.transitTimeToNext() != null ? item.transitTimeToNext() : 0))
            .toList();

    CreateCommand command =
        new CreateCommand(
            input.userId(),
            input.title(),
            input.description(),
            input.totalCost(),
            true, // active
            itemCommands);

    return createItineraryUseCase.create(command);
  }

  // Place del item del itinerario en SchemaMapping (al nivel de la clase, no anidado)
  @SchemaMapping(typeName = "ItineraryItem", field = "place")
  public Place place(ItineraryItem item) {
    return placeRepository.findById(item.getPlaceId()).orElse(null);
  }

  public record CreateItineraryInput(
      UUID userId,
      String title,
      String description,
      double totalCost,
      List<CreateItineraryItemInput> items) {}

  public record CreateItineraryItemInput(
      UUID placeId,
      int sequenceOrder,
      Integer durationInMinutes,
      String notes,
      String transportToNext,
      Integer transitTimeToNext) {}
}
