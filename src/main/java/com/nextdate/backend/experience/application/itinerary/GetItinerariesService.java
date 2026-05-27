package com.nextdate.backend.experience.application.itinerary;

import com.nextdate.backend.experience.domain.Itinerary;
import com.nextdate.backend.experience.domain.ItineraryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetItinerariesService implements GetItinerariesUseCase {
  private final ItineraryRepository itineraryRepository;

  public GetItinerariesService(ItineraryRepository itineraryRepository) {
    this.itineraryRepository = itineraryRepository;
  }

  @Override
  public List<Itinerary> getByUserId(UUID userId) {
    return itineraryRepository.findByUserId(userId);
  }

  @Override
  public Optional<Itinerary> getById(UUID id) {
    return itineraryRepository.findById(id);
  }
}
