package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "places")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaceJpaEntity {

  @Id private UUID id;

  @Column(nullable = false, length = 150)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private PlaceCategory category;

  @Enumerated(EnumType.STRING)
  @Column(name = "price_range", nullable = false, length = 50)
  private PriceRange priceRange;

  @Column(length = 255)
  private String address;

  @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
  private Point location;

  @Column(nullable = false)
  private boolean active;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;
}
