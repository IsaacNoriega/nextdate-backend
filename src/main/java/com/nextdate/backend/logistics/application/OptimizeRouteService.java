package com.nextdate.backend.logistics.application;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.logistics.domain.SpatialService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OptimizeRouteService implements OptimizeRouteUseCase {
    
    private final SpatialService spatialService;

    @Override
    public List<Place> optimizeRoute(List<Place> places) {
        
        if(places == null || places.size() <= 1){
            return places;
        }

        List<Place> unvisited = new ArrayList<>(places);
        List<Place> optimized = new ArrayList<>();
        
        Place current= unvisited.remove(0);
        optimized.add(current);


        while(!unvisited.isEmpty()){
            Place nearest = null;
            double minDistance = Double.MAX_VALUE;


            for(Place candidate : unvisited){
                double distance = spatialService.calculateDistanceInMeters(current.getLocation(), candidate.getLocation());

                if(distance < minDistance){
                    minDistance = distance;
                    nearest = candidate;
                }
            }

            if(nearest != null){
                unvisited.remove(nearest);
                optimized.add(nearest);
                current = nearest;
            }
            else{
                break;
            }
        }
        return optimized;
    }
}
