package com.nextdate.backend.logistics.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.experience.domain.SharedExperienceRepository;
import com.nextdate.backend.experience.domain.TransportType;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.util.Collections;
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
class RecommendItineraryServiceTest {

  @Mock private ProfileRepository profileRepository;
  @Mock private SharedExperienceRepository sharedExperienceRepository;
  @Mock private CreateItineraryUseCase createItineraryUseCase;
  @Mock private AiConciergeClient aiConciergeClient;

  private ObjectMapper objectMapper;
  private RecommendItineraryService recommendItineraryService;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    recommendItineraryService =
        new RecommendItineraryService(
            profileRepository,
            sharedExperienceRepository,
            createItineraryUseCase,
            aiConciergeClient,
            objectMapper);
  }

  @Test
  @DisplayName("1. Happy Path: Debería generar y crear un itinerario recomendado correctamente")
  void deberiaRecomendarEItinerarioCorrectamente() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID placeId = UUID.randomUUID();
    String prompt = "Cita romántica en un café cerca del parque";

    Profile profile = Profile.builder().id(UUID.randomUUID()).userId(userId).bio("Bio").build();
    List<SharedExperience> activeExperiences = Collections.emptyList();

    String mockJsonResponse =
        """
        {
          "title": "Cita Romántica",
          "description": "Una velada increíble",
          "totalCost": 450.0,
          "items": [
            {
              "placeId": "%s",
              "sequenceOrder": 1,
              "durationMinutes": 60,
              "notes": "Tomar un café",
              "transportToNext": "WALKING",
              "transitTimeToNext": 10
            }
          ]
        }
        """.formatted(placeId);

    Itinerary expectedItinerary =
        Itinerary.builder().id(UUID.randomUUID()).title("Cita Romántica").build();

    when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(sharedExperienceRepository.findAllActive()).thenReturn(activeExperiences);
    when(aiConciergeClient.generateItineraryJson(profile, activeExperiences, prompt))
        .thenReturn(mockJsonResponse);
    when(createItineraryUseCase.create(any(CreateItineraryUseCase.CreateCommand.class)))
        .thenReturn(expectedItinerary);

    // Act
    Itinerary result = recommendItineraryService.recommend(userId, prompt);

    // Assert
    assertNotNull(result, "El itinerario retornado no debe ser nulo");
    assertEquals("Cita Romántica", result.getTitle());

    verify(profileRepository, times(1)).findByUserId(userId);
    verify(sharedExperienceRepository, times(1)).findAllActive();
    verify(aiConciergeClient, times(1)).generateItineraryJson(profile, activeExperiences, prompt);
    verify(createItineraryUseCase, times(1)).create(any(CreateItineraryUseCase.CreateCommand.class));
  }

  @Test
  @DisplayName(
      "3 & 4. Negativo/Excepción: Debería lanzar IllegalArgumentException cuando el perfil de usuario no existe")
  void deberiaLanzarExcepcionCuandoPerfilNoExiste() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String prompt = "Cita rápida";

    when(profileRepository.findByUserId(userId)).thenReturn(Optional.empty());

    // Act & Assert
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> recommendItineraryService.recommend(userId, prompt));

    assertTrue(exception.getMessage().contains("Perfil no encontrado"));
    verify(profileRepository, times(1)).findByUserId(userId);
    verifyNoInteractions(sharedExperienceRepository);
    verifyNoInteractions(aiConciergeClient);
    verifyNoInteractions(createItineraryUseCase);
  }

  @Test
  @DisplayName(
      "4. Excepciones: Debería lanzar RuntimeException cuando el JSON de la IA es inválido")
  void deberiaLanzarRuntimeExceptionCuandoJsonEsInvalido() {
    // Arrange
    UUID userId = UUID.randomUUID();
    String prompt = "Cita romántica";
    Profile profile = Profile.builder().id(UUID.randomUUID()).userId(userId).build();

    when(profileRepository.findByUserId(userId)).thenReturn(Optional.of(profile));
    when(sharedExperienceRepository.findAllActive()).thenReturn(Collections.emptyList());
    when(aiConciergeClient.generateItineraryJson(any(), any(), any()))
        .thenReturn("INVALID_JSON_RESPONSE");

    // Act & Assert
    RuntimeException exception =
        assertThrows(
            RuntimeException.class,
            () -> recommendItineraryService.recommend(userId, prompt));

    assertTrue(exception.getMessage().contains("Error al procesar recomendación de la IA"));
    verify(aiConciergeClient, times(1)).generateItineraryJson(any(), any(), any());
    verifyNoInteractions(createItineraryUseCase);
  }
}
