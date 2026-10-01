package com.coop.onboarding.infrastructure.persistence.adapter;

import com.coop.onboarding.application.user.port.UserOutputPort;
import com.coop.onboarding.domain.model.User;
import com.coop.onboarding.infrastructure.persistence.entity.UserEntity;
import com.coop.onboarding.infrastructure.persistence.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class UserPersistenceAdapter implements UserOutputPort {

    private final UserRepository userRepository;

    public UserPersistenceAdapter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public java.util.List<User> findAll() {
        return userRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<User> findByKeycloakId(String keycloakId) {
        return userRepository.findByKeycloakId(keycloakId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        userRepository.deleteById(id);
    }

    @Override
    public User save(User user) {
        UserEntity entity = UserEntity.builder()
                .id(user.getId())
                .keycloakId(user.getKeycloakId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .department(user.getDepartment())
                .build();

        UserEntity saved = userRepository.save(entity);
        return toDomain(saved);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .keycloakId(entity.getKeycloakId())
                .name(entity.getName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .department(entity.getDepartment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
