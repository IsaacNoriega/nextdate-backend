package com.nextdate.backend.logistics.application;

import com.nextdate.backend.experience.domain.TransportType;

public interface EstimateTravelTimeUseCase {
    int estimateTravelTimeInMinutes(double distanceInMeters, TransportType transportType);
}
