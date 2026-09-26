package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.application.user.GetOrCreateUserCommand;
import com.coop.onboarding.application.user.GetOrCreateUserUseCase;
import com.coop.onboarding.domain.model.User;
import com.coop.onboarding.domain.model.UserRole;
import com.coop.onboarding.infrastructure.web.dto.UserProfileResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final GetOrCreateUserUseCase getOrCreateUserUseCase;

    public UserController(GetOrCreateUserUseCase getOrCreateUserUseCase) {
        this.getOrCreateUserUseCase = getOrCreateUserUseCase;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String email = jwt.getClaimAsString("email");
        if (email == null || email.isBlank()) {
            email = keycloakId + "@coop.local";
        }

        String name = jwt.getClaimAsString("name");
        if (name == null || name.isBlank()) {
            name = jwt.getClaimAsString("preferred_username");
            if (name == null || name.isBlank()) {
                name = "Usuário";
            }
        }

        String department = jwt.getClaimAsString("department");
        UserRole role = resolveRoleFromJwt(jwt);

        GetOrCreateUserCommand command = new GetOrCreateUserCommand(
                keycloakId,
                name,
                email,
                role,
                department
        );

        User user = getOrCreateUserUseCase.execute(command);
        return ResponseEntity.ok(UserProfileResponse.fromDomain(user));
    }

    @SuppressWarnings("unchecked")
    private UserRole resolveRoleFromJwt(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj instanceof Collection<?> roles) {
                if (roles.contains("ROLE_ADMIN") || roles.contains("ADMIN")) {
                    return UserRole.ADMIN;
                }
                if (roles.contains("ROLE_GESTOR") || roles.contains("GESTOR")) {
                    return UserRole.GESTOR;
                }
                if (roles.contains("ROLE_COLABORADOR") || roles.contains("COLABORADOR")) {
                    return UserRole.COLABORADOR;
                }
            }
        }
        return UserRole.COLABORADOR;
    }
}
