package com.nextdate.backend.experience.infrastructure.persistence;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceJpaRepository extends JpaRepository<PlaceJpaEntity, UUID> {

  @Query(
      value =
          "SELECT p.* FROM places p WHERE "
              + "ST_DWithin(p.location::geography, ST_SetSRID(ST_Point(:lon,:lat),4326)::geography, :radiusInMeters) = true "
              + "AND p.active = true "
              + "AND (:category IS NULL OR p.category = :category)",
      nativeQuery = true)
  List<PlaceJpaEntity> findNearby(
      @Param("lon") double longitude,
      @Param("lat") double latitude,
      @Param("radiusInMeters") double radiusInMeters,
      @Param("category") String category);

  @Query(
      value =
          "SELECT p.* FROM places p WHERE "
              + "ST_DWithin(p.location::geography, ST_SetSRID(ST_Point(:lon,:lat),4326)::geography, :radiusInMeters) = true "
              + "AND p.active = true "
              + "AND LOWER(TRIM(p.name)) = LOWER(TRIM(:name)) "
              + "LIMIT 1",
      nativeQuery = true)
  java.util.Optional<PlaceJpaEntity> findNearbyMatchingName(
      @Param("lon") double longitude,
      @Param("lat") double latitude,
      @Param("radiusInMeters") double radiusInMeters,
      @Param("name") String name);
}
