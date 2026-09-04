package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PlaceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class PlaceRepositoryAdapter implements PlaceRepository {

  private final PlaceJpaRepository jpaRepository;

  public PlaceRepositoryAdapter(PlaceJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  @Override
  public Place save(Place place) {
    PlaceJpaEntity entity = toJpa(place);
    PlaceJpaEntity saved = jpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public Optional<Place> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public List<Place> findAllById(Iterable<UUID> ids) {
    return jpaRepository.findAllById(ids).stream().map(this::toDomain).toList();
  }

  @Override
  public List<Place> findNearby(
      double latitude, double longitude, double radiusInKm, PlaceCategory category) {
    double radiusInMeters = radiusInKm * 1000;
    String categoryStr = (category != null) ? category.name() : null;

    return jpaRepository.findNearby(longitude, latitude, radiusInMeters, categoryStr).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public Optional<Place> findNearbyMatchingName(
      String name, double latitude, double longitude, double radiusInMeters) {
    if (name == null || name.isBlank()) {
      return Optional.empty();
    }
    return jpaRepository.findNearbyMatchingName(longitude, latitude, radiusInMeters, name.trim())
        .map(this::toDomain);
  }

  private Place toDomain(PlaceJpaEntity entity) {
    return Place.builder()
        .id(entity.getId())
        .name(entity.getName())
        .description(entity.getDescription())
        .category(entity.getCategory())
        .priceRange(entity.getPriceRange())
        .address(entity.getAddress())
        .location(entity.getLocation())
        .active(entity.isActive())
        .createdAt(entity.getCreatedAt())
        .build();
  }

  private PlaceJpaEntity toJpa(Place domain) {
    return PlaceJpaEntity.builder()
        .id(domain.getId())
        .name(domain.getName())
        .description(domain.getDescription())
        .category(domain.getCategory())
        .priceRange(domain.getPriceRange())
        .address(domain.getAddress())
        .location(domain.getLocation())
        .active(domain.isActive())
        .createdAt(domain.getCreatedAt())
        .build();
  }
}
