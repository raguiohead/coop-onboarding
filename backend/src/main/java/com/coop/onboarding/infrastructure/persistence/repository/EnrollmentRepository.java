package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.domain.model.EnrollmentStatus;
import com.coop.onboarding.infrastructure.persistence.entity.EnrollmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnrollmentRepository extends JpaRepository<EnrollmentEntity, UUID> {
    Optional<EnrollmentEntity> findByUserIdAndTrackId(UUID userId, UUID trackId);
    List<EnrollmentEntity> findByUserId(UUID userId);
    List<EnrollmentEntity> findByTrackId(UUID trackId);
    List<EnrollmentEntity> findByStatus(EnrollmentStatus status);
}
