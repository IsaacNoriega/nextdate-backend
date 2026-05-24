package com.nextdate.backend.auth.infrastructure;

// Importar librerias jpa para manejo de base de datos  
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Importar librerias de java para manejo de datos
import java.util.Optional;
import java.util.UUID;


// Definir el repositorio de usuarios  
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    Optional<UserJpaEntity> findByEmail(String email);
}