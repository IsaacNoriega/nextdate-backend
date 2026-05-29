package com.nextdate.backend.logistics.domain;

import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.SharedExperience;
import java.util.List;


public interface AiConciergeClient {
    String generateItineraryJson(
        Profile profile,
        List<SharedExperience> sharedExperiences, 
        String userPrompt
    );
}
