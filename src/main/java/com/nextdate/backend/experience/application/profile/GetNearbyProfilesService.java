package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetNearbyProfilesService implements GetNearbyProfilesUseCase {

  private final ProfileRepository profileRepository;

  public GetNearbyProfilesService(ProfileRepository profileRepository) {
    this.profileRepository = profileRepository;
  }

  @Override
  public List<Profile> getNearby(double longitude, double latitude, double radiusInKm) {
    return profileRepository.findNearby(longitude, latitude, radiusInKm);
  }
}
