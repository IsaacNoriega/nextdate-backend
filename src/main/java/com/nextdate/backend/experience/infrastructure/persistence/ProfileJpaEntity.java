package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileJpaEntity {

  @Id private UUID id;

  @Column(name = "user_id", nullable = false, unique = true)
  private UUID userId;

  @Column(nullable = false)
  private String username;

  @Column(nullable = false)
  private LocalDate birthdate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Gender gender;

  @Column(columnDefinition = "TEXT")
  private String bio;

  @Column(columnDefinition = "geometry(Point, 4326)")
  private Point location;

  @Column(nullable = false)
  private Boolean active;

  @Enumerated(EnumType.STRING)
  @Column(name = "dietary_preference", nullable = false)
  private DietaryPreference dietaryPreference;

  @Enumerated(EnumType.STRING)
  @Column(name = "preferred_price_range", nullable = false)
  private PriceRange preferredPriceRange;

  @ElementCollection(targetClass = PlaceCategory.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "profile_interests", joinColumns = @JoinColumn(name = "profile_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "interest", nullable = false)
  private Set<PlaceCategory> interests;
}
