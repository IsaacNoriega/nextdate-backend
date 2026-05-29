package com.nextdate.backend.experience.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharedExperienceJpaRepository
    extends JpaRepository<SharedExperienceJpaEntity, UUID> {
  List<SharedExperienceJpaEntity> findByActiveTrue();

  List<SharedExperienceJpaEntity> findByUserId(UUID userId);
}
