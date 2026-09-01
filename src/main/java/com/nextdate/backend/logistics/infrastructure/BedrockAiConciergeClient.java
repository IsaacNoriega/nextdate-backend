package com.nextdate.backend.logistics.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Component
public class BedrockAiConciergeClient implements AiConciergeClient {

  private final String modelId;
  private final ObjectProvider<BedrockRuntimeClient> bedrockClientProvider;
  private final ObjectMapper objectMapper;

  public BedrockAiConciergeClient(
      @Value("${aws.bedrock.model-id}") String modelId,
      ObjectProvider<BedrockRuntimeClient> bedrockClientProvider,
      ObjectMapper objectMapper) {
    this.modelId = modelId;
    this.bedrockClientProvider = bedrockClientProvider;
    this.objectMapper = objectMapper;
  }

  @Override
  public String generateItineraryJson(
      Profile profile, List<SharedExperience> experiences, String userPrompt) {

    BedrockRuntimeClient client = bedrockClientProvider.getIfAvailable();
    if (client == null) {
      throw new IllegalStateException(
          "BedrockRuntimeClient no está disponible. Verifique la configuración aws.bedrock.");
    }

    // 1. Construir el contexto RAG inyectando las experiencias
    String experiencesContext =
        experiences.stream()
            .map(
                exp ->
                    String.format(
                        "- ExpID: %s, Title: %s, Budget: %f, Rating: %d, Tips: %s, PlacesInItinerary: %s",
                        exp.getId(),
                        exp.getTitle(),
                        exp.getActualCost(),
                        exp.getRating(),
                        exp.getTips(),
                        exp.getItineraryId()))
            .collect(Collectors.joining("\n"));

    // 2. Construir la consulta del prompt con instrucciones del formato JSON de salida
    String systemPrompt =
        "You are the NextDate AI Concierge. Design a detailed sequential itinerary matching the user profile and situation request. "
            + "Select ONLY valid places or build the logic based on the user's preferences.\n"
            + "Respond ONLY with a valid JSON block (no markdown, no quotes outside JSON, no other text) matching the schema:\n"
            + "{\n"
            + "  \"title\": \"itinerary title\",\n"
            + "  \"description\": \"long description\",\n"
            + "  \"totalCost\": 150.00,\n"
            + "  \"items\": [\n"
            + "    {\n"
            + "      \"placeId\": \"UUID of the selected place\",\n"
            + "      \"sequenceOrder\": 1,\n"
            + "      \"durationInMinutes\": 60,\n"
            + "      \"notes\": \"brief advice note\",\n"
            + "      \"transportToNext\": \"WALKING/DRIVING/TRANSIT/CYCLING/NONE\",\n"
            + "      \"transitTimeToNext\": 15\n"
            + "    }\n"
            + "  ]\n"
            + "}";

    String userMessage =
        String.format(
            "User Profile:\n- Username: %s\n- Diet: %s\n- Price Preference: %s\n- Interests: %s\n\n"
                + "Shared Community Experiences (RAG Database Context):\n%s\n\n"
                + "Request Situation: %s",
            profile.getUsername(),
            profile.getDietaryPreference(),
            profile.getPreferredPriceRange(),
            profile.getInterests(),
            experiencesContext,
            userPrompt);

    try {
      // Body para Claude 3 (Anthropic Messages API) serializado limpiamente con Jackson
      Map<String, Object> payloadMap =
          Map.of(
              "anthropic_version",
              "bedrock-2023-05-31",
              "max_tokens",
              2000,
              "system",
              systemPrompt,
              "messages",
              List.of(Map.of("role", "user", "content", userMessage)),
              "temperature",
              0.2);

      String payload = objectMapper.writeValueAsString(payloadMap);

      InvokeModelRequest request =
          InvokeModelRequest.builder()
              .modelId(modelId)
              .body(SdkBytes.fromUtf8String(payload))
              .contentType("application/json")
              .build();

      InvokeModelResponse response = client.invokeModel(request);
      String responseBody = response.body().asString(StandardCharsets.UTF_8);

      // Deserialización tipada con Jackson
      return parseTextFromClaudeResponse(responseBody);
    } catch (Exception e) {
      throw new RuntimeException(
          "Error invocando el modelo de IA en Bedrock: " + e.getMessage(), e);
    }
  }

  private String parseTextFromClaudeResponse(String responseJson) {
    try {
      JsonNode rootNode = objectMapper.readTree(responseJson);
      JsonNode contentNode = rootNode.path("content");
      if (contentNode.isArray() && !contentNode.isEmpty()) {
        String generatedText = contentNode.get(0).path("text").asText();
        if (generatedText != null && !generatedText.isBlank()) {
          return generatedText.trim();
        }
      }
    } catch (Exception e) {
      // Fallback si no es formato Claude standard
    }
    return responseJson;
  }
}
