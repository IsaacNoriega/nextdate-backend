package com.nextdate.backend.logistics.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

class GeminiAiConciergeClientTest {

  @Test
  @DisplayName(
      "parseTextFromGeminiResponse extrae correctamente el JSON desde el payload de Google Gemini")
  void shouldParseJsonFromGeminiResponse() {
    ObjectMapper objectMapper = new ObjectMapper();
    RestClient.Builder builder = RestClient.builder();
    GeminiAiConciergeClient client =
        new GeminiAiConciergeClient(
            "test-key",
            "gemini-3.6-flash",
            "https://generativelanguage.googleapis.com",
            objectMapper,
            builder);

    String expectedAiJson =
        """
        {"title": "Cena Romántica Italiana", "description": "Plan especial para dos", "totalCost": 350.0, "items": []}
        """;

    String rawGeminiResponse =
        """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "%s"
                  }
                ],
                "role": "model"
              },
              "finishReason": "STOP"
            }
          ]
        }
        """
            .formatted(expectedAiJson.replace("\"", "\\\"").replace("\n", ""));

    String parsed = client.parseTextFromGeminiResponse(rawGeminiResponse);

    assertThat(parsed).contains("Cena Romántica Italiana");
    assertThat(parsed).contains("350.0");
  }

  @Test
  @DisplayName(
      "parseTextFromGeminiResponse limpia delimitadores de markdown si Gemini incluye ```json")
  void shouldCleanMarkdownFencesIfPresent() {
    ObjectMapper objectMapper = new ObjectMapper();
    RestClient.Builder builder = RestClient.builder();
    GeminiAiConciergeClient client =
        new GeminiAiConciergeClient(
            "test-key",
            "gemini-3.6-flash",
            "https://generativelanguage.googleapis.com",
            objectMapper,
            builder);

    String rawWithMarkdown =
        """
        {
          "candidates": [
            {
              "content": {
                "parts": [
                  {
                    "text": "```json\\n{\\"title\\": \\"Paseo por el Parque\\", \\"totalCost\\": 0.0}\\n```"
                  }
                ]
              }
            }
          ]
        }
        """;

    String parsed = client.parseTextFromGeminiResponse(rawWithMarkdown);

    assertThat(parsed).isEqualTo("{\"title\": \"Paseo por el Parque\", \"totalCost\": 0.0}");
  }

  @Test
  @DisplayName("Debe lanzar excepción si apiKey está vacía o es nula")
  void shouldThrowWhenApiKeyIsMissing() {
    ObjectMapper objectMapper = new ObjectMapper();
    RestClient.Builder builder = RestClient.builder();
    GeminiAiConciergeClient client =
        new GeminiAiConciergeClient(
            "",
            "gemini-3.6-flash",
            "https://generativelanguage.googleapis.com",
            objectMapper,
            builder);

    Profile profile =
        Profile.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .username("isaac_dev")
            .birthdate(LocalDate.of(1995, 1, 1))
            .gender(Gender.MALE)
            .dietaryPreference(DietaryPreference.NONE)
            .preferredPriceRange(PriceRange.CHEAP)
            .interests(Set.of())
            .build();

    SharedExperience exp =
        SharedExperience.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .itineraryId(UUID.randomUUID())
            .title("Picnic")
            .actualCost(100.0)
            .rating(5)
            .tips("Llevar mantel")
            .build();

    assertThatThrownBy(
            () -> client.generateItineraryJson(profile, List.of(exp), "Plan para una tarde"))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("GEMINI_API_KEY no está configurada");
  }
}
