package com.coop.onboarding.infrastructure.persistence.repository;

import com.coop.onboarding.infrastructure.persistence.entity.LessonProgressEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonProgressRepository extends JpaRepository<LessonProgressEntity, UUID> {
    Optional<LessonProgressEntity> findByEnrollmentIdAndLessonId(UUID enrollmentId, UUID lessonId);
    List<LessonProgressEntity> findByEnrollmentId(UUID enrollmentId);
    long countByEnrollmentIdAndCompletedTrue(UUID enrollmentId);
}
