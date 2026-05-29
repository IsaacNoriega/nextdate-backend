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
public class SharedExperience {
  private UUID id;
  private UUID userId;
  private UUID itineraryId;
  private String title;
  private String description;
  private String tips;
  private double actualCost;
  private int rating;
  private Boolean active;
  private LocalDateTime createdAt;
  private List<String> imageUrls;
}
