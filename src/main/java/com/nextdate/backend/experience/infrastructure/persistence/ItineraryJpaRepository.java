package com.nextdate.backend.experience.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryJpaRepository extends JpaRepository<ItineraryJpaEntity, UUID> {
  List<ItineraryJpaEntity> findByUserId(UUID userId);
}
