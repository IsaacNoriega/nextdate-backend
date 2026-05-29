package com.nextdate.backend.experience.application.itinerary;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryItem;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.TransportType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CreateItineraryService implements CreateItineraryUseCase {

  private final ItineraryRepository itineraryRepository;
  private final PlaceRepository placeRepository;

  public CreateItineraryService(
      ItineraryRepository itineraryRepository, PlaceRepository placeRepository) {
    this.itineraryRepository = itineraryRepository;
    this.placeRepository = placeRepository;
  }

  @Override
  public Itinerary create(CreateCommand command) {
    // 1. Validar Place Ids
    List<ItineraryItem> domainItems =
        command.items().stream()
            .map(
                item -> {
                  placeRepository
                      .findById(item.placeId())
                      .orElseThrow(
                          () ->
                              new IllegalArgumentException(
                                  "Lugar no encontrado con ID: " + item.placeId()));

                  return ItineraryItem.builder()
                      .id(UUID.randomUUID())
                      .placeId(item.placeId())
                      .sequenceOrder(item.sequenceOrder())
                      .durationInMinutes(
                          item.durationInMinutes() > 0 ? item.durationInMinutes() : 60)
                      .notes(item.notes())
                      .transportToNext(
                          item.transportToNext() != null
                              ? item.transportToNext()
                              : TransportType.NONE)
                      .transitTimeToNext(item.transitTimeToNext())
                      .build();
                })
            .toList();

    Itinerary itinerary =
        Itinerary.builder()
            .id(UUID.randomUUID())
            .userId(command.userId())
            .title(command.title())
            .description(command.description())
            .totalCost(command.totalCost())
            .active(command.active())
            .createdAt(LocalDateTime.now())
            .items(domainItems)
            .build();

    return itineraryRepository.save(itinerary);
  }
}
