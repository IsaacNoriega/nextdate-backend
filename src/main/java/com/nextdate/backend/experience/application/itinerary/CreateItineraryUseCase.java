package com.nextdate.backend.experience.application.itinerary;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.TransportType;
import java.util.List;
import java.util.UUID;

public interface CreateItineraryUseCase {

  Itinerary create(CreateCommand command);

  record CreateCommand(
      UUID userId,
      String title,
      String description,
      double totalCost,
      boolean active,
      List<CreateItemCommand> items) {}
  ;

  record CreateItemCommand(
      UUID placeId,
      int sequenceOrder,
      int durationInMinutes,
      String notes,
      TransportType transportToNext,
      int transitTimeToNext) {}
  ;
}
