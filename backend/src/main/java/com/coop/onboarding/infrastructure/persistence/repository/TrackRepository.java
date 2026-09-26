package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.infrastructure.persistence.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<TrackEntity, UUID> {
    List<TrackEntity> findByIsActiveTrue();
    List<TrackEntity> findByTargetDepartmentAndIsActiveTrue(String targetDepartment);
}
