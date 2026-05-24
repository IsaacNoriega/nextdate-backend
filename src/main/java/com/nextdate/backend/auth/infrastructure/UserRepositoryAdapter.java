package com.nextdate.backend.auth.infrastructure;

// Importacion de User y UserRepository del dominio
import com.nextdate.backend.auth.domain.User;
import com.nextdate.backend.auth.domain.UserRepository;

// Librerias para el repositorio de usuarios  
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

// Definir el repositorio de usuarios  
@Component
public class UserRepositoryAdapter implements UserRepository {
    
    // Repositorio de usuarios  
    private final UserJpaRepository jpaRepository;

    // Constructor para la inyeccion de dependencias  
    public UserRepositoryAdapter(UserJpaRepository jpaRepository){
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<User>findById(UUID id){
        // si existe el usuario, lo mapea a dominio
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<User>findByEmail(String email){
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user){
        // Mapea y guarda el usuario en la base de datos  
        UserJpaEntity jpaEntity = toJpa(user);
        UserJpaEntity savedEntity = jpaRepository.save(jpaEntity);
        return toDomain(savedEntity);
    }

    // Traduce un UserJpaEntity a User  
    private User toDomain(UserJpaEntity entity){
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .active(entity.isActive())
                .build();
    }


    // Traduce un User a UserJpaEntity  
    private UserJpaEntity toJpa(User domain){
        return UserJpaEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .passwordHash(domain.getPasswordHash())
                .active(domain.isActive())
                .build();
    }
}
