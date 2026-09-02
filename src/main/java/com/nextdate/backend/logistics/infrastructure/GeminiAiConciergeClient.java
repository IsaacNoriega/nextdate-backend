package com.nextdate.backend.logistics.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GeminiAiConciergeClient implements AiConciergeClient {

  private final String apiKey;
  private final String model;
  private final String baseUrl;
  private final ObjectMapper objectMapper;
  private final RestClient restClient;

  public GeminiAiConciergeClient(
      @Value("${gemini.api-key:}") String apiKey,
      @Value("${gemini.model:gemini-1.5-flash}") String model,
      @Value("${gemini.base-url:https://generativelanguage.googleapis.com}") String baseUrl,
      ObjectMapper objectMapper,
      RestClient.Builder restClientBuilder) {
    this.apiKey = apiKey;
    this.model = model;
    this.baseUrl = baseUrl;
    this.objectMapper = objectMapper;
    this.restClient = restClientBuilder.baseUrl(baseUrl).build();
  }

  @Override
  public String generateItineraryJson(
      Profile profile, List<SharedExperience> experiences, String userPrompt) {

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException(
          "GEMINI_API_KEY no está configurada. Verifique las variables de entorno o configure gemini.mock=true.");
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

    // 2. Construir el system prompt con instrucciones del formato JSON de salida
    String systemPrompt =
        "You are the NextDate AI Concierge. Design a detailed sequential itinerary matching the user profile and situation request. "
            + "Select ONLY valid places or build the logic based on the user's preferences.\n"
            + "Respond ONLY with a valid JSON block matching the schema:\n"
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
      // Estructura oficial del payload para Google Gemini REST API
      Map<String, Object> requestPayload =
          Map.of(
              "system_instruction",
              Map.of("parts", List.of(Map.of("text", systemPrompt))),
              "contents",
              List.of(Map.of("role", "user", "parts", List.of(Map.of("text", userMessage)))),
              "generationConfig",
              Map.of("response_mime_type", "application/json", "temperature", 0.2));

      String responseJson =
          restClient
              .post()
              .uri("/v1beta/models/{model}:generateContent?key={apiKey}", model, apiKey)
              .contentType(MediaType.APPLICATION_JSON)
              .body(requestPayload)
              .retrieve()
              .body(String.class);

      return parseTextFromGeminiResponse(responseJson);
    } catch (Exception e) {
      throw new RuntimeException("Error invocando el modelo de IA en Gemini: " + e.getMessage(), e);
    }
  }

  String parseTextFromGeminiResponse(String responseJson) {
    try {
      JsonNode rootNode = objectMapper.readTree(responseJson);
      JsonNode candidates = rootNode.path("candidates");
      if (candidates.isArray() && !candidates.isEmpty()) {
        JsonNode parts = candidates.get(0).path("content").path("parts");
        if (parts.isArray() && !parts.isEmpty()) {
          String generatedText = parts.get(0).path("text").asText();
          if (generatedText != null && !generatedText.isBlank()) {
            return cleanJsonFormatting(generatedText.trim());
          }
        }
      }
    } catch (Exception e) {
      // Fallback si no es formato Gemini estándar
    }
    return cleanJsonFormatting(responseJson);
  }

  private String cleanJsonFormatting(String rawText) {
    String text = rawText.trim();
    if (text.startsWith("```json")) {
      text = text.substring(7);
    } else if (text.startsWith("```")) {
      text = text.substring(3);
    }
    if (text.endsWith("```")) {
      text = text.substring(0, text.length() - 3);
    }
    return text.trim();
  }
}
