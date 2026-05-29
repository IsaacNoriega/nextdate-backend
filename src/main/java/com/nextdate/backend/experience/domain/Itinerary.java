package com.nextdate.backend.experience.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class Itinerary {
  private UUID id;
  private UUID userId;
  private String title;
  private String description;
  private double totalCost;
  private Boolean active;
  private LocalDateTime createdAt;
  private List<ItineraryItem> items;
}
