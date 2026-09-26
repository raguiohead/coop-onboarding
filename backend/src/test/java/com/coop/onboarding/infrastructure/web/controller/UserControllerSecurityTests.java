package com.coop.onboarding.infrastructure.web.controller;

import com.coop.onboarding.TestcontainersConfiguration;
import com.coop.onboarding.application.user.GetOrCreateUserCommand;
import com.coop.onboarding.application.user.GetOrCreateUserUseCase;
import com.coop.onboarding.domain.model.User;
import com.coop.onboarding.domain.model.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class UserControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GetOrCreateUserUseCase getOrCreateUserUseCase;

    @Test
    @DisplayName("Anônimo recebe 401 Unauthorized ao tentar GET /api/v1/users/me")
    void anonymousReceives401OnGetMe() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Usuário autenticado com JWT recupera ou cria perfil via GET /api/v1/users/me")
    void authenticatedUserGetsProfile() throws Exception {
        String keycloakId = "user-kc-123";
        User user = User.builder()
                .id(UUID.randomUUID())
                .keycloakId(keycloakId)
                .name("Maria Silva")
                .email("maria.silva@coop.local")
                .role(UserRole.COLABORADOR)
                .department("Atendimento")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(getOrCreateUserUseCase.execute(any(GetOrCreateUserCommand.class))).thenReturn(user);

        mockMvc.perform(get("/api/v1/users/me")
                        .with(jwt().jwt(jwt -> jwt
                                .subject(keycloakId)
                                .claim("name", "Maria Silva")
                                .claim("email", "maria.silva@coop.local")
                                .claim("realm_access", Map.of("roles", List.of("COLABORADOR")))
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keycloakId").value(keycloakId))
                .andExpect(jsonPath("$.name").value("Maria Silva"))
                .andExpect(jsonPath("$.email").value("maria.silva@coop.local"))
                .andExpect(jsonPath("$.role").value("COLABORADOR"));
    }
}
