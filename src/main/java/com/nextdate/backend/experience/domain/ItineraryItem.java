package com.nextdate.backend.experience.domain;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ItineraryItem {
  private UUID id;
  private UUID placeId;
  private int sequenceOrder;
  private int durationInMinutes;
  private String notes;
  private TransportType transportToNext;
  private int transitTimeToNext;
}
