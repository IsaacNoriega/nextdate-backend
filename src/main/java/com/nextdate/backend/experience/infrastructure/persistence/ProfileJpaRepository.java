package com.nextdate.backend.experience.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, UUID> {

  Optional<ProfileJpaEntity> findByUserId(UUID userId);

  // Consulta nativa para busqueda por distancia
  @Query(
      value =
          "SELECT * FROM profiles p WHERE "
              + "ST_DWithin(p.location::geography, ST_SetSRID(ST_Point(:lon, :lat), 4326)::geography, :radiusInMeters) = true "
              + "AND p.active = true",
      nativeQuery = true)
  List<ProfileJpaEntity> findNearby(
      @Param("lon") double longitude,
      @Param("lat") double latitude,
      @Param("radiusInMeters") double radiusInMeters);
}
