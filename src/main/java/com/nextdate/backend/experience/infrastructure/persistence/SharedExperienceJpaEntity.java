package com.nextdate.backend.experience.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "shared_experiences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedExperienceJpaEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false)
  private UUID userId;

  @Column(name = "itinerary_id", nullable = false)
  private UUID itineraryId;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "TEXT")
  private String tips;

  @Column(name = "actual_cost", nullable = false)
  private BigDecimal actualCost;

  @Column(nullable = false)
  private Integer rating;

  @Column(nullable = false)
  private Boolean active;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
      name = "shared_experience_images",
      joinColumns = @JoinColumn(name = "shared_experience_id"))
  @Column(name = "image_url", nullable = false)
  private List<String> imageUrls;
}
