package com.nextdate.backend.experience.application.place;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nextdate.backend.experience.application.place.CreatePlaceUseCase.CreateCommand;
import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.PriceRange;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreatePlaceServiceTest {

  @Mock private PlaceRepository placeRepository;

  private CreatePlaceService createPlaceService;

  @BeforeEach
  void setUp() {
    createPlaceService = new CreatePlaceService(placeRepository);
  }

  @Test
  void shouldReturnExistingPlaceWhenDuplicateFoundWithin100Meters() {
    UUID existingId = UUID.randomUUID();
    Place existingPlace =
        Place.builder()
            .id(existingId)
            .name("Café Central")
            .category(PlaceCategory.FOOD_DRINK)
            .priceRange(PriceRange.MODERATE)
            .address("Av. Vallarta 123")
            .active(true)
            .createdAt(LocalDateTime.now())
            .build();

    when(placeRepository.findNearbyMatchingName("Café Central", 20.6745, -103.3702, 100.0))
        .thenReturn(Optional.of(existingPlace));

    CreateCommand command =
        new CreateCommand(
            "Café Central",
            "Café acogedor",
            PlaceCategory.FOOD_DRINK,
            PriceRange.MODERATE,
            "Av. Vallarta 123",
            20.6745,
            -103.3702);

    Place result = createPlaceService.create(command);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(existingId);
    assertThat(result.getName()).isEqualTo("Café Central");
    verify(placeRepository, never()).save(any(Place.class));
  }

  @Test
  void shouldCreateNewPlaceWhenNoDuplicateExists() {
    when(placeRepository.findNearbyMatchingName("Nuevo Bistro", 20.6745, -103.3702, 100.0))
        .thenReturn(Optional.empty());

    when(placeRepository.save(any(Place.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    CreateCommand command =
        new CreateCommand(
            "Nuevo Bistro",
            "Bistro nuevo",
            PlaceCategory.FOOD_DRINK,
            PriceRange.EXPENSIVE,
            "Calle 10 #200",
            20.6745,
            -103.3702);

    Place result = createPlaceService.create(command);

    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Nuevo Bistro");
    assertThat(result.getCategory()).isEqualTo(PlaceCategory.FOOD_DRINK);
    verify(placeRepository).save(any(Place.class));
  }
}
