package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceRepository;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class MockAiConciergeClient implements AiConciergeClient {

  private final PlaceRepository placeRepository;

  public MockAiConciergeClient(PlaceRepository placeRepository) {
    this.placeRepository = placeRepository;
  }

  @Override
  public String generateItineraryJson(
      Profile profile, List<SharedExperience> sharedExperiences, String userPrompt) {

    // Genera un texto con la información de los lugares cercanos al usuario
    String placeIdStr = "244c4fae-9d22-49bd-9154-159424c52af6"; // Fallback por defecto
    List<Place> allPlaces = placeRepository.findNearby(20.6745, -103.3702, 100.0, null);

    if (!allPlaces.isEmpty()) {
      placeIdStr = allPlaces.get(0).getId().toString(); // Fallback por defecto
    }

    return "{\n"
        + "  \"title\": \"Cita Recomendada: "
        + userPrompt
        + "\",\n"
        + "  \"description\": \"Itinerario simulado basado en tus gustos: "
        + profile.getUsername()
        + "\",\n"
        + "  \"totalCost\": 120.00,\n"
        + "  \"items\": [\n"
        + "    {\n"
        + "      \"placeId\": \""
        + placeIdStr
        + "\",\n"
        + "      \"sequenceOrder\": 1,\n"
        + "      \"durationInMinutes\": 60,\n"
        + "      \"notes\": \"Mock stop for coffee and chat based on community tips\",\n"
        + "      \"transportToNext\": \"WALKING\",\n"
        + "      \"transitTimeToNext\": 15\n"
        + "    }\n"
        + "  ]\n"
        + "}";
  }
}
