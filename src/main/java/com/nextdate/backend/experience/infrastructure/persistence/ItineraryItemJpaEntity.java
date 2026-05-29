package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.TransportType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "itinerary_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItineraryItemJpaEntity {

  @Id private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "itinerary_id", nullable = false)
  private ItineraryJpaEntity itinerary;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "place_id", nullable = false)
  private PlaceJpaEntity place;

  @Column(name = "sequence_order", nullable = false)
  private int sequenceOrder;

  @Column(name = "duration_in_minutes", nullable = false)
  private int durationInMinutes;

  @Column(columnDefinition = "TEXT")
  private String notes;

  @Enumerated(EnumType.STRING)
  @Column(name = "transport_to_next", nullable = false)
  private TransportType transportToNext;

  @Column(name = "transit_time_to_next", nullable = false)
  private int transitTimeToNext;
}
