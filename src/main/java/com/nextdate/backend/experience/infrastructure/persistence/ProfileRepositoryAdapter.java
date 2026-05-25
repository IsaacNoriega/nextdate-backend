package com.nextdate.backend.experience.infrastructure.persistence;

import com.nextdate.backend.experience.domain.Profile;
import com.nextdate.backend.experience.domain.ProfileRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ProfileRepositoryAdapter implements ProfileRepository {

  private final ProfileJpaRepository jpaRepository;

  public ProfileRepositoryAdapter(ProfileJpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
  }

  // Busca el perfil por Id
  @Override
  public Optional<Profile> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  // Busca el perfil por Id de usuario
  @Override
  public Optional<Profile> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).map(this::toDomain);
  }

  // Guarda un perfil o actualiza si ya existe (upsert)
  @Override
  public Profile save(Profile profile) {
    ProfileJpaEntity jpaEntity = toJpa(profile);
    ProfileJpaEntity savedJpaEntity = jpaRepository.save(jpaEntity);
    return toDomain(savedJpaEntity);
  }

  @Override
  public List<Profile> findNearby(double longitude, double latitude, double radiusInKm) {

    double radiusInMeters = radiusInKm * 1000.0; // 1km = 1000m para hacer calculos mas precisos

    return jpaRepository.findNearby(longitude, latitude, radiusInMeters).stream()
        .map(this::toDomain)
        .toList();
  }

  // Convierte una entidad Jpa a un perfil
  private Profile toDomain(ProfileJpaEntity entity) {
    return Profile.builder()
        .id(entity.getId())
        .userId(entity.getUserId())
        .username(entity.getUsername())
        .birthdate(entity.getBirthdate())
        .gender(entity.getGender())
        .bio(entity.getBio())
        .location(entity.getLocation())
        .active(entity.getActive())
        .build();
  }

  // Convierte un perfil a una entidad Jpa
  private ProfileJpaEntity toJpa(Profile profile) {
    return ProfileJpaEntity.builder()
        .id(profile.getId())
        .userId(profile.getUserId())
        .username(profile.getUsername())
        .birthdate(profile.getBirthdate())
        .gender(profile.getGender())
        .bio(profile.getBio())
        .location(profile.getLocation())
        .active(profile.getActive())
        .build();
  }
}
