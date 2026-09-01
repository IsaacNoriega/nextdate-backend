package com.nextdate.backend.logistics.domain;

import org.locationtech.jts.geom.Point;

public interface SpatialService {

  double calculateDistanceInMeters(Point p1, Point p2);
}
