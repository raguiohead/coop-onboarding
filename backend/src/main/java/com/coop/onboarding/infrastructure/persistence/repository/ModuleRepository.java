package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.infrastructure.persistence.entity.ModuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<ModuleEntity, UUID> {
    List<ModuleEntity> findByTrackIdOrderByOrderIndexAsc(UUID trackId);
}
