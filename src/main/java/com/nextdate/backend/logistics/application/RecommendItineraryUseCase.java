package com.nextdate.backend.logistics.application;


import com.nextdate.backend.experience.domain.Itinerary;
import java.util.UUID;

public interface RecommendItineraryUseCase {
    Itinerary recommend(UUID userId , String prompt);
}
