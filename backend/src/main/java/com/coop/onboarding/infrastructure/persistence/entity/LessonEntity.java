package com.coop.onboarding.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "lessons", uniqueConstraints = {
    @UniqueConstraint(name = "uq_module_lesson_order", columnNames = {"module_id", "order_index"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "module_id", nullable = false)
    private ModuleEntity module;

    @Column(nullable = false)
    private String title;

    @Column(name = "content_markdown", nullable = false, columnDefinition = "TEXT")
    private String contentMarkdown;

    @Column(name = "video_url", length = 500)
    private String videoUrl;

    @Column(name = "estimated_minutes", nullable = false)
    @Builder.Default
    private Integer estimatedMinutes = 15;

    @Column(name = "order_index", nullable = false)
    @Builder.Default
    private Integer orderIndex = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
