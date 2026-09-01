package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryItem;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ItineraryRepositoryAdapter implements ItineraryRepository {

  private final ItineraryJpaRepository itineraryJpaRepository;
  private final PlaceJpaRepository placeJpaRepository;

  public ItineraryRepositoryAdapter(
      ItineraryJpaRepository itineraryJpaRepository, PlaceJpaRepository placeJpaRepository) {
    this.itineraryJpaRepository = itineraryJpaRepository;
    this.placeJpaRepository = placeJpaRepository;
  }

  // Guardar un itinerario
  @Override
  public Itinerary save(Itinerary itinerary) {
    ItineraryJpaEntity jpaEntity = toJpa(itinerary);
    ItineraryJpaEntity savedJpaEntity = itineraryJpaRepository.save(jpaEntity);
    return toDomain(savedJpaEntity);
  }

  // Buscar un itinerario específico por ID
  @Override
  public Optional<Itinerary> findById(UUID id) {
    return itineraryJpaRepository.findById(id).map(this::toDomain);
  }

  // Buscar múltiples itinerarios por lista de IDs
  @Override
  public List<Itinerary> findAllById(Iterable<UUID> ids) {
    return itineraryJpaRepository.findAllById(ids).stream().map(this::toDomain).toList();
  }

  // Buscar todos los itinerarios de un usuario específico
  @Override
  public List<Itinerary> findByUserId(UUID userId) {
    return itineraryJpaRepository.findByUserId(userId).stream().map(this::toDomain).toList();
  }

  private Itinerary toDomain(ItineraryJpaEntity entity) {
    List<ItineraryItem> items =
        entity.getItems().stream()
            .map(
                itemEntity ->
                    ItineraryItem.builder()
                        .id(itemEntity.getId())
                        .placeId(itemEntity.getPlace().getId())
                        .sequenceOrder(itemEntity.getSequenceOrder())
                        .durationInMinutes(itemEntity.getDurationInMinutes())
                        .notes(itemEntity.getNotes())
                        .transportToNext(itemEntity.getTransportToNext())
                        .transitTimeToNext(itemEntity.getTransitTimeToNext())
                        .build())
            .toList();

    return Itinerary.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .totalCost(entity.getTotalCost() != null ? entity.getTotalCost().doubleValue() : 0.0)
        .active(entity.getActive())
        .createdAt(entity.getCreatedAt())
        .items(items)
        .build();
  }

  private ItineraryJpaEntity toJpa(Itinerary domain) {
    ItineraryJpaEntity jpaEntity =
        ItineraryJpaEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .title(domain.getTitle())
            .description(domain.getDescription())
            .totalCost(BigDecimal.valueOf(domain.getTotalCost()))
            .active(domain.getActive())
            .createdAt(domain.getCreatedAt())
            .build();

    List<ItineraryItemJpaEntity> itemEntities =
        domain.getItems().stream()
            .map(
                item ->
                    ItineraryItemJpaEntity.builder()
                        .id(item.getId())
                        .itinerary(jpaEntity)
                        .place(placeJpaRepository.getReferenceById(item.getPlaceId()))
                        .sequenceOrder(item.getSequenceOrder())
                        .durationInMinutes(item.getDurationInMinutes())
                        .notes(item.getNotes())
                        .transportToNext(item.getTransportToNext())
                        .transitTimeToNext(item.getTransitTimeToNext())
                        .build())
            .toList();

    jpaEntity.setItems(itemEntities);
    return jpaEntity;
  }
}
