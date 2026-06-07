package com.nextdate.backend.logistics.infrastructure;

import com.nextdate.backend.logistics.domain.SpatialService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;


@Service
public class PostgisSpatialService implements SpatialService {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public double calculateDistanceInMeters(Point p1, Point p2) {
        if(p1 == null || p2 == null) {
            return 0.0;
        }

        String query = "SELECT ST_Distance(cast(:p1 as geography), cast(:p2 as geography))";

        Number result = (Number) entityManager.createNativeQuery(query)
            .setParameter("p1", p1)
            .setParameter("p2", p2)
            .getSingleResult();

        return result != null ? result.doubleValue() : 0.0;
    }



}
