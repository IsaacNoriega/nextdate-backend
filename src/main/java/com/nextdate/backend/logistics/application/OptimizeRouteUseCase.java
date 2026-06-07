package com.nextdate.backend.logistics.application;

import com.nextdate.backend.experience.domain.Place;
import java.util.List;

public interface OptimizeRouteUseCase {
    
    List<Place> optimizeRoute(List<Place> places );
}
