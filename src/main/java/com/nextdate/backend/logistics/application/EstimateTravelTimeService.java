package com.nextdate.backend.logistics.application;

import com.nextdate.backend.experience.domain.TransportType;
import org.springframework.stereotype.Service;

@Service
public class EstimateTravelTimeService implements EstimateTravelTimeUseCase {

    private static final double WALKING_SPEED_M_PM = 80.0;
    private static final double DRIVING_SPEED_M_PM = 600.0;
    private static final double TRANSIT_SPEED_M_PM = 300.0;
    private static final double CYCLING_SPEED_M_PM = 250.0;

    @Override
    public int estimateTravelTimeInMinutes(double distanceInMeters, TransportType transportType) {
        if(distanceInMeters <= 0 || transportType == null || transportType == TransportType.NONE) {
            return 0;
        }

    

    double speed = switch(transportType) {
        case WALKING -> WALKING_SPEED_M_PM;
        case DRIVING -> DRIVING_SPEED_M_PM;
        case TRANSIT -> TRANSIT_SPEED_M_PM;
        case CYCLING -> CYCLING_SPEED_M_PM;
        default -> 0.0;
    };

    if(speed <= 0.0) {
        return 0;
    }

    double time = distanceInMeters / speed;
    int calculatedTime = (int) Math.ceil(time);

    return Math.max(1,calculatedTime);
}
}
