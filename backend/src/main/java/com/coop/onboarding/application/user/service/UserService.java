package com.coop.onboarding.application.user.service;

import com.coop.onboarding.application.user.GetOrCreateUserCommand;
import com.coop.onboarding.application.user.GetOrCreateUserUseCase;
import com.coop.onboarding.application.user.port.UserOutputPort;
import com.coop.onboarding.domain.model.User;
import com.coop.onboarding.domain.model.UserRole;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService implements GetOrCreateUserUseCase {

    private final UserOutputPort userOutputPort;

    public UserService(UserOutputPort userOutputPort) {
        this.userOutputPort = userOutputPort;
    }

    @Override
    public User execute(GetOrCreateUserCommand command) {
        return userOutputPort.findByKeycloakId(command.keycloakId())
                .orElseGet(() -> userOutputPort.findByEmail(command.email())
                        .map(existing -> {
                            User updated = User.builder()
                                    .id(existing.getId())
                                    .keycloakId(command.keycloakId())
                                    .name(command.name())
                                    .email(command.email())
                                    .role(command.role() != null ? command.role() : existing.getRole())
                                    .department(command.department() != null ? command.department() : existing.getDepartment())
                                    .createdAt(existing.getCreatedAt())
                                    .updatedAt(existing.getUpdatedAt())
                                    .build();
                            return userOutputPort.save(updated);
                        })
                        .orElseGet(() -> {
                            User newUser = User.builder()
                                    .keycloakId(command.keycloakId())
                                    .name(command.name())
                                    .email(command.email())
                                    .role(command.role() != null ? command.role() : UserRole.COLABORADOR)
                                    .department(command.department())
                                    .build();
                            return userOutputPort.save(newUser);
                        }));
    }
}
