package com.nextdate.backend.logistics.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nextdate.backend.experience.application.itinerary.CreateItineraryUseCase;
import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import com.nextdate.backend.experience.domain.SharedExperience;
import com.nextdate.backend.experience.domain.SharedExperienceRepository;
import com.nextdate.backend.experience.domain.TransportType;
import com.nextdate.backend.logistics.domain.AiConciergeClient;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;


@Service
public class RecommendItineraryService implements RecommendItineraryUseCase {
    
    private final ProfileRepository profileRepository;
    private final SharedExperienceRepository sharedExperienceRepository;
    private final CreateItineraryUseCase createItineraryUseCase;
    private final AiConciergeClient aiConciergeClient;
    private final ObjectMapper objectMapper;


    public RecommendItineraryService(
        ProfileRepository profileRepository,
        SharedExperienceRepository sharedExperienceRepository,
        CreateItineraryUseCase createItineraryUseCase,
        AiConciergeClient aiConciergeClient,
        ObjectMapper objectMapper
    ){
        this.profileRepository = profileRepository;
        this.sharedExperienceRepository = sharedExperienceRepository;
        this.createItineraryUseCase = createItineraryUseCase;
        this.aiConciergeClient = aiConciergeClient;
        this.objectMapper = objectMapper;
    }


    @Override
    public Itinerary recommend(UUID userId , String prompt){

        // Obtener perfil y los intereses
        Profile profile = profileRepository
            .findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado para el usuario " + userId));

        List<SharedExperience> activeExperiences = sharedExperienceRepository.findAllActive();

        String jsonRecommendation = aiConciergeClient.generateItineraryJson(profile,activeExperiences,prompt);

        try {
            RecommendItineraryJson parsed = objectMapper.readValue(jsonRecommendation,RecommendItineraryJson.class);

            List<CreateItineraryUseCase.CreateItemCommand> itemCommands =
                parsed.items().stream()
                .map(
                    item ->
                        new CreateItineraryUseCase.CreateItemCommand(
                            item.placeId(),
                            item.sequenceOrder(),
                            item.durationMinutes(),
                            item.notes(),
                            item.transportToNext() != null ? TransportType.valueOf(item.transportToNext().toUpperCase()) : TransportType.NONE , item.transitTimeToNext()
                        )
                ).toList();

                CreateItineraryUseCase.CreateCommand createCommand = 
                    new CreateItineraryUseCase.CreateCommand(
                        userId,
                        parsed.title(),
                        parsed.description(),
                        parsed.totalCost(),
                        true,
                        itemCommands
                    );

                    return createItineraryUseCase.create(createCommand);
        }catch (Exception e){
            throw new RuntimeException("Error al procesar recomendación de la IA: " + e.getMessage(), e);
        }
    }

        private record RecommendItineraryJson(
            String title,
            String description,
            double totalCost,
            List<RecommendItemJson> items
        ){}

        private record RecommendItemJson(
            UUID placeId,
            int sequenceOrder,
            int durationMinutes,
            String notes,
            String transportToNext,
            Integer transitTimeToNext
        ){}
}
