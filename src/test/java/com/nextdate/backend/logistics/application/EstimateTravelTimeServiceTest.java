package com.nextdate.backend.logistics.application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nextdate.backend.experience.domain.TransportType;
import org.junit.jupiter.api.Test;

class EstimateTravelTimeServiceTest {

  private final EstimateTravelTimeService estimator = new EstimateTravelTimeService();

  @Test
  void shouldReturnZeroForInvalidInputs() {
    assertEquals(0, estimator.estimateTravelTimeInMinutes(-10, TransportType.WALKING));
    assertEquals(0, estimator.estimateTravelTimeInMinutes(100, null));
    assertEquals(0, estimator.estimateTravelTimeInMinutes(100, TransportType.NONE));
  }

  @Test
  void shouldEstimateTimeForWalking() {
    // 80 metros a 80m/min = 1 minuto
    assertEquals(1, estimator.estimateTravelTimeInMinutes(80, TransportType.WALKING));
    // 120 metros a 80m/min = 1.5 min -> Redondeado a 2 minutos
    assertEquals(2, estimator.estimateTravelTimeInMinutes(120, TransportType.WALKING));
  }

  @Test
  void shouldEstimateTimeForDriving() {
    // 1200 metros a 600m/min = 2 minutos
    assertEquals(2, estimator.estimateTravelTimeInMinutes(1200, TransportType.DRIVING));
  }

  @Test
  void shouldGuaranteeAtLeastOneMinuteIfDistanceGreaterThanZero() {
    assertEquals(1, estimator.estimateTravelTimeInMinutes(5, TransportType.DRIVING));
  }
}
