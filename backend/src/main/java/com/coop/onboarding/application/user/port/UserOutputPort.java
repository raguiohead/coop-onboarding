package com.coop.onboarding.application.user.port;

import com.coop.onboarding.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserOutputPort {
    List<User> findAll();
    Optional<User> findByKeycloakId(String keycloakId);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    User save(User user);
    void deleteById(UUID id);
}
