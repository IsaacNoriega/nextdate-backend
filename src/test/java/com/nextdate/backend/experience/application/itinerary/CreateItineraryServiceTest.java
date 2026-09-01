package com.nextdate.backend.experience.application.itinerary;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.TransportType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CreateItineraryServiceTest {

  @Mock private ItineraryRepository itineraryRepository;
  @Mock private PlaceRepository placeRepository;

  private CreateItineraryService createItineraryService;

  @BeforeEach
  void setUp() {
    createItineraryService = new CreateItineraryService(itineraryRepository, placeRepository);
  }

  @Test
  @DisplayName("1. Happy Path: Debería crear un itinerario correctamente")
  void deberiaCrearItinerarioCorrectamente() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID placeId = UUID.randomUUID();

    CreateItineraryUseCase.CreateItemCommand itemCommand =
        new CreateItineraryUseCase.CreateItemCommand(
            placeId, 1, 90, "Visitar café", TransportType.WALKING, 15);

    CreateItineraryUseCase.CreateCommand createCommand =
        new CreateItineraryUseCase.CreateCommand(
            userId, "Itinerario Fin de Semana", "Descripción", 300.0, true, List.of(itemCommand));

    Place mockPlace = Place.builder().id(placeId).name("Café Central").build();

    when(placeRepository.findById(placeId)).thenReturn(Optional.of(mockPlace));
    when(itineraryRepository.save(any(Itinerary.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Itinerary result = createItineraryService.create(createCommand);

    // Assert
    assertNotNull(result, "El itinerario no debe ser nulo");
    assertEquals("Itinerario Fin de Semana", result.getTitle());
    assertEquals(1, result.getItems().size());
    assertEquals(90, result.getItems().get(0).getDurationInMinutes());
    assertEquals(TransportType.WALKING, result.getItems().get(0).getTransportToNext());

    verify(placeRepository, times(1)).findById(placeId);
    verify(itineraryRepository, times(1)).save(any(Itinerary.class));
  }

  @Test
  @DisplayName(
      "5. Boundary & Default Values: Debería aplicar valores por defecto en duración (60 min) y transporte (NONE)")
  void deberiaAplicarValoresPorDefectoEnItems() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID placeId = UUID.randomUUID();

    // durationInMinutes <= 0 y transportToNext = null
    CreateItineraryUseCase.CreateItemCommand itemCommand =
        new CreateItineraryUseCase.CreateItemCommand(placeId, 1, 0, "Notas", null, 0);

    CreateItineraryUseCase.CreateCommand createCommand =
        new CreateItineraryUseCase.CreateCommand(
            userId, "Itinerario Test", "Desc", 0.0, true, List.of(itemCommand));

    when(placeRepository.findById(placeId))
        .thenReturn(Optional.of(Place.builder().id(placeId).build()));
    when(itineraryRepository.save(any(Itinerary.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    // Act
    Itinerary result = createItineraryService.create(createCommand);

    // Assert
    assertEquals(
        60,
        result.getItems().get(0).getDurationInMinutes(),
        "Duración debe ser 60 min por defecto");
    assertEquals(
        TransportType.NONE,
        result.getItems().get(0).getTransportToNext(),
        "Transporte debe ser NONE por defecto");
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException si un PlaceId no existe")
  void deberiaLanzarExcepcionCuandoLugarNoExiste() {
    // Arrange
    UUID placeIdInexistente = UUID.randomUUID();
    CreateItineraryUseCase.CreateItemCommand itemCommand =
        new CreateItineraryUseCase.CreateItemCommand(
            placeIdInexistente, 1, 60, "Notas", TransportType.NONE, 0);

    CreateItineraryUseCase.CreateCommand createCommand =
        new CreateItineraryUseCase.CreateCommand(
            UUID.randomUUID(), "Itinerario", "Desc", 100.0, true, List.of(itemCommand));

    when(placeRepository.findById(placeIdInexistente)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class, () -> createItineraryService.create(createCommand));

    assertTrue(
        exception.getMessage().contains("Lugar no encontrado con ID: " + placeIdInexistente));
    verify(itineraryRepository, never()).save(any());
  }
}
