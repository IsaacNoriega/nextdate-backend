package com.nextdate.backend.logistics.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.nextdate.backend.experience.domain.Place;
import com.nextdate.backend.experience.domain.PlaceCategory;
import com.nextdate.backend.experience.domain.PriceRange;
import com.nextdate.backend.logistics.domain.SpatialService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptimizeRouteServiceTest {

  @Mock
  private SpatialService spatialService;

  @InjectMocks
  private OptimizeRouteService optimizeRouteService;

  private GeometryFactory geometryFactory;

  @BeforeEach
  void setUp() {
    geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
  }

  @Test
  void shouldReturnSameListWhenEmptyOrSinglePlace() {
    // Escenario vacío
    assertNull(optimizeRouteService.optimizeRoute(null));
    assertEquals(Collections.emptyList(), optimizeRouteService.optimizeRoute(Collections.emptyList()));

    // Escenario un solo elemento
    Place place = createMockPlace(0.0, 0.0);
    List<Place> singleList = List.of(place);
    List<Place> result = optimizeRouteService.optimizeRoute(singleList);

    assertEquals(1, result.size());
    assertEquals(place, result.get(0));
  }

  @Test
  void shouldOptimizeRouteByNearestNeighbor() {
    // Crear 3 lugares
    // Lugar inicial A, Lugar C (cerca de A), Lugar B (lejos de A)
    Place placeA = createMockPlace(0.0, 0.0);
    Place placeB = createMockPlace(10.0, 10.0);
    Place placeC = createMockPlace(1.0, 1.0);

    List<Place> places = List.of(placeA, placeB, placeC);

    // Mockear distancias desde A
    when(spatialService.calculateDistanceInMeters(placeA.getLocation(), placeB.getLocation())).thenReturn(1000.0);
    when(spatialService.calculateDistanceInMeters(placeA.getLocation(), placeC.getLocation())).thenReturn(100.0);

    // Mockear distancias desde C (el más cercano a A se vuelve el actual)
    when(spatialService.calculateDistanceInMeters(placeC.getLocation(), placeB.getLocation())).thenReturn(900.0);

    // Ejecutar ordenación (debe comenzar en A, buscar el más cercano -> C, y luego B)
    List<Place> optimized = optimizeRouteService.optimizeRoute(places);

    assertNotNull(optimized);
    assertEquals(3, optimized.size());
    assertEquals(placeA, optimized.get(0)); // Inicio
    assertEquals(placeC, optimized.get(1)); // Cerca
    assertEquals(placeB, optimized.get(2)); // Lejos
  }

  private Place createMockPlace(double longitude, double latitude) {
    Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));
    return Place.builder()
        .id(UUID.randomUUID())
        .name("Lugar Mock")
        .description("Descripción")
        .category(PlaceCategory.FOOD_DRINK)
        .priceRange(PriceRange.MODERATE)
        .address("Calle Ficticia")
        .location(point)
        .active(true)
        .createdAt(LocalDateTime.now())
        .build();
  }
}
