package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.experience.domain.SharedExperienceRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class SharedExperienceRepositoryAdapter implements SharedExperienceRepository {

  private final SharedExperienceJpaRepository jpaRepository;

  public SharedExperienceRepositoryAdapter(SharedExperienceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public SharedExperience save(SharedExperience experience) {
    SharedExperienceJpaEntity jpaEntity = toJpa(experience);
    SharedExperienceJpaEntity savedEntity = jpaRepository.save(jpaEntity);
    return toDomain(savedEntity);
  }

  @Override
  public Optional<SharedExperience> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<SharedExperience> findAllActive() {
    return jpaRepository.findByActiveTrue().stream().map(this::toDomain).toList();
  }

  @Override
  public List<SharedExperience> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).stream().map(this::toDomain).toList();
  }

  private SharedExperience toDomain(SharedExperienceJpaEntity entity) {
    return SharedExperience.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .itineraryId(entity.getItineraryId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .tips(entity.getTips())
        .actualCost(entity.getActualCost() != null ? entity.getActualCost().doubleValue() : 0.0)
        .rating(entity.getRating())
        .active(entity.getActive())
        .createdAt(entity.getCreatedAt())
        .imageUrls(
            entity.getImageUrls() != null
                ? new ArrayList<>(entity.getImageUrls())
                : new ArrayList<>())
        .build();
  }

  private SharedExperienceJpaEntity toJpa(SharedExperience domain) {
    return SharedExperienceJpaEntity.builder()
        .id(domain.getId())
        .userId(domain.getUserId())
        .itineraryId(domain.getItineraryId())
        .title(domain.getTitle())
        .description(domain.getDescription())
        .tips(domain.getTips())
        .actualCost(BigDecimal.valueOf(domain.getActualCost()))
        .rating(domain.getRating())
        .active(domain.getActive())
        .createdAt(domain.getCreatedAt())
        .imageUrls(
            domain.getImageUrls() != null
                ? new ArrayList<>(domain.getImageUrls())
                : new ArrayList<>())
        .build();
  }
}
