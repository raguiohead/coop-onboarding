package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.infrastructure.persistence.entity.LessonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LessonRepository extends JpaRepository<LessonEntity, UUID> {
    List<LessonEntity> findByModuleIdOrderByOrderIndexAsc(UUID moduleId);
}
