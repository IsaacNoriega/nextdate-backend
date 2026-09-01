package com.nextdate.backend.experience.infrastructure.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase;
import com.nextdate.backend.experience.application.itinerary.GetItinerariesUseCase;
import com.nextdate.backend.experience.application.shared.GetSharedExperiencesUseCase;
import com.nextdate.backend.experience.application.shared.ShareExperienceUseCase;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryItem;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BatchMappingTests {

  @Test
  @DisplayName(
      "ItineraryGraphQLController.@BatchMapping resuelve todos los lugares de los items en una sola consulta por lote")
  void shouldBatchResolvePlacesForItineraryItemsInSingleQuery() {
    // Arrange
    PlaceRepository placeRepository = mock(PlaceRepository.class);
    GetItinerariesUseCase getItinerariesUseCase = mock(GetItinerariesUseCase.class);
    CreateItineraryUseCase createItineraryUseCase = mock(CreateItineraryUseCase.class);

    ItineraryGraphQLController controller =
        new ItineraryGraphQLController(
            getItinerariesUseCase, createItineraryUseCase, placeRepository);

    UUID place1Id = UUID.randomUUID();
    UUID place2Id = UUID.randomUUID();

    Place place1 =
        Place.builder()
            .id(place1Id)
            .name("Restaurante Bar")
            .category(PlaceCategory.FOOD_DRINK)
            .priceRange(PriceRange.MODERATE)
            .address("Av Chapultepec")
            .build();

    Place place2 =
        Place.builder()
            .id(place2Id)
            .name("Café Galería")
            .category(PlaceCategory.CULTURE)
            .priceRange(PriceRange.CHEAP)
            .address("Calle Libertad")
            .build();

    ItineraryItem item1 =
        ItineraryItem.builder().id(UUID.randomUUID()).placeId(place1Id).sequenceOrder(1).build();

    ItineraryItem item2 =
        ItineraryItem.builder().id(UUID.randomUUID()).placeId(place2Id).sequenceOrder(2).build();

    ItineraryItem item3 =
        ItineraryItem.builder().id(UUID.randomUUID()).placeId(place1Id).sequenceOrder(3).build();

    when(placeRepository.findAllById(any())).thenReturn(List.of(place1, place2));

    // Act
    Map<ItineraryItem, Place> result = controller.place(List.of(item1, item2, item3));

    // Assert: Se llama al repositorio exactamente 1 vez para todo el lote
    verify(placeRepository, times(1)).findAllById(Set.of(place1Id, place2Id));
    assertThat(result).hasSize(3);
    assertThat(result.get(item1)).isEqualTo(place1);
    assertThat(result.get(item2)).isEqualTo(place2);
    assertThat(result.get(item3)).isEqualTo(place1);
  }

  @Test
  @DisplayName(
      "SharedExperienceGraphQLController.@BatchMapping resuelve todos los itinerarios de las experiencias en una sola consulta")
  void shouldBatchResolveItinerariesForSharedExperiencesInSingleQuery() {
    // Arrange
    ItineraryRepository itineraryRepository = mock(ItineraryRepository.class);
    ShareExperienceUseCase shareExperienceUseCase = mock(ShareExperienceUseCase.class);
    GetSharedExperiencesUseCase getSharedExperiencesUseCase =
        mock(GetSharedExperiencesUseCase.class);

    SharedExperienceGraphQLController controller =
        new SharedExperienceGraphQLController(
            shareExperienceUseCase, getSharedExperiencesUseCase, itineraryRepository);

    UUID itin1Id = UUID.randomUUID();
    UUID itin2Id = UUID.randomUUID();

    Itinerary itin1 =
        Itinerary.builder()
            .id(itin1Id)
            .userId(UUID.randomUUID())
            .title("Itinerario Romántico")
            .build();
    Itinerary itin2 =
        Itinerary.builder().id(itin2Id).userId(UUID.randomUUID()).title("Tarde Cultural").build();

    SharedExperience exp1 =
        SharedExperience.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .itineraryId(itin1Id)
            .title("Gran experiencia 1")
            .build();

    SharedExperience exp2 =
        SharedExperience.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .itineraryId(itin2Id)
            .title("Gran experiencia 2")
            .build();

    when(itineraryRepository.findAllById(any())).thenReturn(List.of(itin1, itin2));

    // Act
    Map<SharedExperience, Itinerary> result = controller.itinerary(List.of(exp1, exp2));

    // Assert: Se llama al repositorio exactamente 1 vez para todo el lote
    verify(itineraryRepository, times(1)).findAllById(Set.of(itin1Id, itin2Id));
    assertThat(result).hasSize(2);
    assertThat(result.get(exp1)).isEqualTo(itin1);
    assertThat(result.get(exp2)).isEqualTo(itin2);
  }
}
