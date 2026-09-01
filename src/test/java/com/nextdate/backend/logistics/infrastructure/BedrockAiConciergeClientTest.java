package com.nextdate.backend.logistics.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.domain.DietaryPreference;
import com.nextdate.backend.experience.domain.Gender;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

class BedrockAiConciergeClientTest {

  @Test
  @DisplayName(
      "BedrockAiConciergeClient serializa prompt con ObjectMapper y deserializa JSON de Claude correctamente")
  void shouldSerializePromptAndDeserializeClaudeResponse() {
    // Arrange
    BedrockRuntimeClient mockBedrockClient = mock(BedrockRuntimeClient.class);
    @SuppressWarnings("unchecked")
    ObjectProvider<BedrockRuntimeClient> provider = mock(ObjectProvider.class);
    when(provider.getIfAvailable()).thenReturn(mockBedrockClient);

    ObjectMapper objectMapper = new ObjectMapper();

    BedrockAiConciergeClient client =
        new BedrockAiConciergeClient(
            "anthropic.claude-3-haiku-20240307-v1:0", provider, objectMapper);

    Profile profile =
        Profile.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .username("ana_gdl")
            .birthdate(LocalDate.of(1998, 5, 20))
            .gender(Gender.FEMALE)
            .dietaryPreference(DietaryPreference.VEGETARIAN)
            .preferredPriceRange(PriceRange.MODERATE)
            .interests(java.util.Set.of())
            .build();

    SharedExperience exp =
        SharedExperience.builder()
            .id(UUID.randomUUID())
            .userId(UUID.randomUUID())
            .itineraryId(UUID.randomUUID())
            .title("Cena romántica")
            .actualCost(450.0)
            .rating(5)
            .tips("Pedir mesa en terraza")
            .build();

    String expectedAiJson =
        """
        {"title": "Cena Vegetariana & Terraza", "description": "Plan romántico", "totalCost": 450.0, "items": []}
        """;

    String rawClaudeResponse =
        """
        {
          "id": "msg_123",
          "type": "message",
          "role": "assistant",
          "content": [
            {
              "type": "text",
              "text": "%s"
            }
          ]
        }
        """
            .formatted(expectedAiJson.replace("\"", "\\\"").replace("\n", ""));

    InvokeModelResponse mockResponse =
        InvokeModelResponse.builder()
            .body(SdkBytes.fromString(rawClaudeResponse, StandardCharsets.UTF_8))
            .build();

    when(mockBedrockClient.invokeModel(any(InvokeModelRequest.class))).thenReturn(mockResponse);

    // Act
    String generatedJson =
        client.generateItineraryJson(profile, List.of(exp), "Plan tranquilo de cena");

    // Assert
    assertThat(generatedJson).contains("Cena Vegetariana & Terraza");
    assertThat(generatedJson).contains("450.0");
  }
}
