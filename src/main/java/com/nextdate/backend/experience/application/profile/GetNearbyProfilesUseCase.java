package com.nextdate.backend.experience.application.profile;

import com.nextdate.backend.experience.domain.Profile;
import java.util.List;

public interface GetNearbyProfilesUseCase {
  List<Profile> getNearby(double longitude, double latitude, double radiusInKm);
}
