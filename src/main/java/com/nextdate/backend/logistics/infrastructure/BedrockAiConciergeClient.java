package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

@Component
public class BedrockAiConciergeClient implements AiConciergeClient {

  private final String modelId;
  private final String region;
  private final String accessKey;
  private final String secretKey;

  public BedrockAiConciergeClient(
      @Value("${aws.bedrock.model-id}") String modelId,
      @Value("${aws.bedrock.region}") String region,
      @Value("${aws.credentials.access-key}") String accessKey,
      @Value("${aws.credentials.secret-key}") String secretKey) {
    this.modelId = modelId;
    this.region = region;
    this.accessKey = accessKey;
    this.secretKey = secretKey;
  }

  @Override
  public String generateItineraryJson(
      Profile profile, List<SharedExperience> experiences, String userPrompt) {

    // Crear el cliente de AWS Bedrock Runtime
    StaticCredentialsProvider credentialsProvider =
        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));

    try (BedrockRuntimeClient client =
        BedrockRuntimeClient.builder()
            .region(Region.of(region))
            .credentialsProvider(credentialsProvider)
            .build()) {

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

      // Body para Claude 3 (Anthropic Messages API)
      String payload =
          "{\n"
              + "  \"anthropic_version\": \"bedrock-2023-05-31\",\n"
              + "  \"max_tokens\": 2000,\n"
              + "  \"system\": \""
              + systemPrompt.replace("\"", "\\\"").replace("\n", "\\n")
              + "\",\n"
              + "  \"messages\": [\n"
              + "    {\"role\": \"user\", \"content\": \""
              + userMessage.replace("\"", "\\\"").replace("\n", "\\n")
              + "\"}\n"
              + "  ],\n"
              + "  \"temperature\": 0.2\n"
              + "}";

      InvokeModelRequest request =
          InvokeModelRequest.builder()
              .modelId(modelId)
              .body(SdkBytes.fromUtf8String(payload))
              .contentType("application/json")
              .build();

      InvokeModelResponse response = client.invokeModel(request);
      String responseBody = response.body().asString(StandardCharsets.UTF_8);

      // Extraer el texto de la respuesta de Claude 3
      // Nota: Para mantenerlo simple y sin dependencias pesadas adicionales,
      // buscamos el texto dentro de content[0].text
      return parseTextFromClaudeResponse(responseBody);
    }
  }

  private String parseTextFromClaudeResponse(String responseJson) {
    // Claude 3 devuelve un JSON que contiene: "content": [{"type": "text", "text":
    // "JSON_GENERADO"}]
    // Un parseo manual simple de strings para extraer el JSON puro sin meter librerías complejas:
    int startIdx = responseJson.indexOf("\"text\":\"");
    if (startIdx == -1) {
      return responseJson;
    }
    startIdx += 8;
    int endIdx = responseJson.lastIndexOf("\"");
    if (endIdx <= startIdx) {
      return responseJson;
    }
    String escapedJson = responseJson.substring(startIdx, endIdx);
    // Remover escapes de comillas y saltos de línea devueltos por el JSON de AWS
    return escapedJson.replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", " ").trim();
  }
}
