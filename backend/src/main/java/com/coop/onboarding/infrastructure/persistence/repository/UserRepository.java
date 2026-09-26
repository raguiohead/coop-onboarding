package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    Optional<UserEntity> findByKeycloakId(String keycloakId);
    Optional<UserEntity> findByEmail(String email);
    boolean existsByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
}
