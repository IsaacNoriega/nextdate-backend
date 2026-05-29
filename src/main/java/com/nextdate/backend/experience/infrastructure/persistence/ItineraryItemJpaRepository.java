package com.nextdate.backend.experience.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItineraryItemJpaRepository extends JpaRepository<ItineraryItemJpaEntity, UUID> {}
